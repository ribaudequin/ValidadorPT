# Relatório de Auditoria — Validador PT (v0.1.0 Beta)

**Data:** 2026-09-28  
**Projeto:** Validador PT — App Android 100% offline para validação estrutural de NIF, IBAN e NIB portugueses  
**Versão auditada:** 0.1.0 (MVP Beta)  
**Stack:** Kotlin 2.1.10 + Jetpack Compose (BOM 2025.02.00) + MVVM + Gradle KTS 8.8.0  
**APK:** ~6.2 MB (assinado com debug keystore)  
**Testes:** 34 testes JUnit5 (18 NIF + 6 IBAN + 10 NIB) — BUILD PASS

---

## 1. Resumo Executivo

O **Validador PT** é um MVP bem executado para validação offline de identificadores fiscais/bancários portugueses. O projeto cumpre o objetivo declarado: validar matematicamente NIF (9 dígitos), IBAN PT (25 chars) e NIB (21 dígitos), identificar tipo de entidade (NIF) e banco (via `banks.json` com 32 entradas), e exibir disclaimer legal permanente.

**Pontos fortes:**
- Arquitetura limpa (MVVM + Compose), separação clara de validadores, repositório e UI
- Zero permissões de rede, zero tracking — verdadeiramente offline-first
- i18n completo (PT-PT base + EN fallback via resource qualifiers)
- Testes unitários abrangentes cobrindo casos válidos, inválidos e edge cases
- Build reproduzível, APK assinado, release GitHub criada

**Principais riscos/limitações atuais:**
1. **Keystore de debug** em produção — não adequado para distribuição real
2. **Falta suporte a tema escuro** (hardcoded `darkTheme = false`)
3. **Crédito Agrícola** — múltiplos códigos (0045, 5180, 5200, etc.) podem confundir utilizador
4. **NIB legado** — oficialmente substituído por IBAN em 2016; manter ou não?
5. **Strings com aspas literais** nos XMLs (ex: `"NIF (9 dígitos)"`) — bug visual

---

## 2. Análise Técnica Detalhada

### 2.1 Validadores (Core Logic)

| Validador | Algoritmo | Cobertura Testes | Observações |
|-----------|-----------|------------------|-------------|
| **NifValidator** | Módulo 11 (pesos 9..2) | 18 testes | ✅ Completo: tipos entidade, dígito controlo, edge cases (resto 0/1→0, prefixos 0/4) |
| **IbanValidator** | Módulo 97 (ISO 13616) | 6 testes | ✅ Válido apenas PT; extrai código banco (pos 4-7); resolve nome via BankRepository |
| **NibValidator** | Módulo 97 (pesos fixos 19) | 10 testes | ✅ 21 dígitos; extrai código banco (pos 0-3); **atenção: NIB descontinuado desde 2016** |

**Qualidade do código:** Boa. Lógica pura, sem dependências Android, testável isoladamente. Uso de `sealed class ValidationResult` para type-safety.

**Melhorias sugeridas:**
- Adicionar validação de checksum IBAN genérica (não só PT) para future-proofing
- Documentar fonte dos pesos NIB (Banco de Portugal Instrução 12/96)
- Considerar cache do `banks.json` parsed (já é singleton, ok)

---

### 2.2 Arquitetura & Camadas

```
ui/screens/           → ValidatorApp, TabContent (NIF/IBAN/NIB), ResultCard
ui/viewmodels/       → ValidatorViewModel + Factory (StateFlow + viewModelScope)
ui/state/            → ValidatorUiState (data class simples)
validator/           → NifValidator, IbanValidator, NibValidator (objects)
data/                → BankRepository (singleton, assets/banks.json), Bank model
util/                → LocaleManager, Extensions
ui/theme/            → ValidadorPTTheme (light/dark ColorScheme)
```

**Pontos positivos:**
- Injeção de dependência manual via Factory (sem Hilt/Koin — mantém APK pequeno)
- StateFlow unidirecional, sem side effects na UI
- `BankRepository` carrega JSON assíncrono na primeira chamada (assets → memory)

**Riscos:**
- `ValidatorViewModel` faz parsing de string no `validateIban/validateNib` — mistura validação + formatação. Melhor: retornar `ValidationResult` e formatar na UI.
- `ResultCard` usa string matching (`contains("válido")`) para cor — frágil com i18n. Deveria receber `ValidationResult` tipado.

---

### 2.3 Interface & UX

**Estrutura:**
- `CenterAlignedTopAppBar` + `TabRow` (3 tabs) + conteúdo scrollável + disclaimer fixo amarelo
- `OutlinedTextField` por tab, botão "Validar", `ResultCard` colorido (verde/vermelho)

**Problemas identificados:**
1. **Strings com aspas nos XMLs** — `hint_nif="NIF (9 dígitos)"` renderiza aspas literais na UI
2. **Tema escuro desativado** — `ValidadorPTTheme(darkTheme = false)` hardcoded; ignora `isSystemInDarkTheme()`
3. **Disclaimer amarelo fixo** — bom para legal, mas poderia ser `Snackbar` ou `Dialog` no primeiro uso
4. **Sem feedback tátil/haptic** no botão validar
5. **Keyboard IME "Done"** valida automaticamente — bom UX

**Acessibilidade:**
- `contentDescription` ausente em ícones/launcher
- Contraste do disclaimer (preto sobre amarelo #FFEB3B) — OK (ratio ~12:1)
- Tamanhos de toque adequados (Material3 defaults)

---

### 2.4 Internacionalização (i18n)

| Resource | Idioma | Status |
|----------|--------|--------|
| `values/strings.xml` | EN (base) | ✅ Completo |
| `values-pt/strings.xml` | PT-PT | ✅ Completo |
| `values-en/strings.xml` | EN (fallback) | ✅ Completo |

**LocaleManager:** Aplica `pt-PT` se `system language == "pt"`, senão default. **Problema:** não distingue `pt-BR` de `pt-PT` — usuários BR verão PT-PT. Sugestão: checar `locale.country == "PT"`.

---

### 2.5 Dados — `banks.json` (32 bancos)

**Cobertura:** Inclui bancos principais (CGD, BCP, BPI, Santander, Montepio, etc.) + Crédito Agrícola (3 códigos) + alguns extintos/fundidos (BANIF, BES → Novo Banco).

**Qualidade:**
- Códigos de 4 dígitos (formato oficial Banco de Portugal)
- Campo `swift` opcional (null para alguns) — bem tratado no parser
- **Gap:** Faltam códigos recentes (ex: Banco CTT 0193 está, mas 0269 Bankinter é sucursal)

**Recomendação:** Adicionar script de atualização automática a partir da lista oficial do Banco de Portugal (CSV público) para releases futuras.

---

### 2.6 Build & Configuração

| Item | Valor | Nota |
|------|-------|------|
| `compileSdk` / `targetSdk` | 35 | ✅ Atual |
| `minSdk` | 21 | ✅ >99% dispositivos |
| `kotlinCompilerExtensionVersion` | 1.7.3 | ✅ Compatível Compose BOM 2025.02 |
| JDK | 17 (Temurin) | ⚠️ Java 25 no sistema **incompatível** — documentado no MEMORY.md |
| Signing | Debug keystore | 🔴 **Crítico** para release real |
| Minify/ProGuard | Desativado | OK para beta; ativar em release |
| Configuration cache | Ativado | ✅ Performance |

---

### 2.7 Testes

**Cobertura:** 34 testes JUnit5 puros (sem Robolectric/Espresso) — validadores apenas.

| Suite | Testes | Cobertura |
|-------|--------|-----------|
| NifValidatorTest | 18 | Válidos (6 tipos), inválidos (6), vazios (2), edge (4) |
| IbanValidatorTest | 6 | Válido (1), inválidos (3), vazio (1), extração código (1) |
| NibValidatorTest | 10 | Válidos (3), inválidos (4), extração código (3), vazio (1) |

**Gaps:**
- Zero testes de UI (Compose Testing)
- Zero testes de integração ViewModel + Repository
- Zero testes de `LocaleManager` / `BankRepository`
- **Recomendação:** Adicionar testes Compose para `ResultCard`, `ValidatorApp` (tab switching, disclaimer visível)

---

## 3. Checklist de Segurança & Privacidade

| Item | Status | Comentário |
|------|--------|------------|
| Permissões Internet | ❌ Nenhuma declarada | ✅ Offline-first real |
| Permissões armazenamento | ❌ Nenhuma | ✅ Sem persistência |
| Tracking/Analytics | ❌ Nenhum | ✅ Privacidade total |
| Keystore produção | 🔴 Debug | **Bloqueador para Play Store** |
| ProGuard/R8 | ⚠️ Desativado | Ativar em release |
| Network Security Config | N/A | Sem rede |
| Dados sensíveis em logs | ✅ Nenhum | Validação só em memória |

---

## 4. Recomendações Priorizadas

### 🔴 Crítico (Antes de qualquer release público)
1. **Gerar keystore de produção** — substituir debug keystore em `build.gradle.kts` (linha 27-30)
2. **Corrigir strings XML** — remover aspas literais de todos `strings.xml` (linhas 7-14)
3. **Ativar `minifyEnabled = true`** + ProGuard rules para release

### 🟠 Alto (Próxima iteração)
4. **Implementar tema escuro** — usar `isSystemInDarkTheme()` no `ValidadorPTTheme`
5. **Refatorar ViewModel** — retornar `ValidationResult` tipado, não `String`; formatar na UI
6. **Corrigir `LocaleManager`** — distinguir `pt-PT` vs `pt-BR` (checar `country`)
7. **Adicionar testes Compose** — `ResultCard`, navegação tabs, disclaimer

### 🟡 Médio (Roadmap v0.2+)
8. **Script atualização `banks.json`** — automação a partir de fonte oficial BdP
9. **Reavaliar NIB** — manter apenas para legado? Documentar claramente
10. **Crédito Agrícola UX** — agrupar códigos 0045/5180/5200+ sob "Crédito Agrícola" com subcódigo
11. **Feedback háptico** — `performHapticFeedback` no botão validar
12. **Acessibilidade** — `contentDescription` nos ícones, testar TalkBack

### 🟢 Baixo (Nice to have)
13. **Exportar resultado** (copiar/partilhar)
14. **Histórico local** (opcional, Room/SharedPrefs) — se utilizador pedir
15. **Validação IBAN genérica** (outros países) — extensível via config
16. **CI/CD GitHub Actions** — build + test + assinar + release automático

---

## 5. Comentário Geral sobre o App

> **O Validador PT é um exemplo raro de "software bem-feito" no ecossistema mobile atual: faz uma coisa, faz bem, sem ruído.**

**O que admiro:**
- **Foco cirúrgico** — NIF/IBAN/NIB, nada mais. Sem login, sem cloud, sem anúncios, sem permissões.
- **Decisões técnicas conscientes** — Kotlin/Compose/MVVM foi a escolha certa para APK ~6MB (vs Flutter ~15MB, RN ~20MB+). JDK 17 documentado como requisito não-negoiciável.
- **Disclaimer honesto** — "validação estrutural ≠ existência real" sempre visível. Isso protege o utilizador e o desenvolvedor.
- **Testes reais** — 34 testes unitários não são "checkbox"; cobrem edge cases matemáticos (resto 0/1 no módulo 11, prefixos NIF inválidos 0/4).

**O que me preocupa (como utilizador):**
1. **NIB é legado** — desde 2016 Portugal usa IBAN. Manter NIB confunde utilizadores leigos ("qual uso?"). Sugestão: esconder por default, mostrar só se toggle "Modo legado" ou remover.
2. **Crédito Agrícola** — 3 códigos diferentes (0045, 5180, 5200) aparecem como bancos separados. Quem tem conta no CA vê "Caixa de Leiria" ou "Caixa de Mafra" e pode achar que errou.
3. **Sem tema escuro** — em 2026 é expectativa básica; o amarelo do disclaimer cega em modo escuro.

**Veredito:** **Pronto para beta testing em dispositivo real.** Os bugs críticos (strings, keystore) são correções de 10 min cada. A arquitetura suporta evolução sem reescrita. Se o objetivo é "app de utilidade pública portuguesa", este MVP cumpre — e com código que dá gosto ler.

---

## 6. Próximos Passos Sugeridos (Ordem)

1. `+memoria` — atualizar MEMORY.md com findings desta auditoria
2. Corrigir strings.xml (remover aspas)
3. Gerar keystore produção (`keytool -genkeypair...`)
4. Ativar tema escuro + minify
5. Rodar testes em device físico (instalar APK release)
6. Criar GitHub Actions workflow para CI/CD
7. Planejar v0.2 com roadmap do PLANO.md

---

*Relatório gerado automaticamente via auditoria de código estático + análise de documentação do projeto.*