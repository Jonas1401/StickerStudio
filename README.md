# Sticker Studio

Aplicativo Android (Kotlin, Jetpack Compose, Material 3, Room) para criar figurinhas para WhatsApp.

## Estado: FASE 1

Implementado: navegação, importação de foto pelo Photo Picker (sem permissões), validação do conteúdo real da imagem,
armazenamento privado + miniaturas, Room (pacotes, figurinhas, projetos), CRUD de pacotes (criar, renomear, excluir
com confirmação), tema claro/escuro, testes unitários de `ImageMath`.

Ainda NÃO implementado (próximas fases): editor, exportação WebP, integração com WhatsApp (ContentProvider),
vídeo/GIF, remoção de fundo. Os botões de vídeo e GIF aparecem desativados ("Em breve").

## Compilar

Requisitos: Android Studio (Ladybug ou mais recente), JDK 17.

1. Abra a pasta `StickerStudio` no Android Studio e aguarde o sync (ele gera o Gradle Wrapper se faltar;
   por linha de comando, rode `gradle wrapper --gradle-version 8.9` uma vez).
2. Debug: `./gradlew assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`
3. Testes unitários: `./gradlew testDebugUnitTest`

## Dependências principais

AGP 8.7.3 · Kotlin 2.0.21 · KSP 2.0.21-1.0.28 · Compose BOM 2024.12.01 · Navigation Compose 2.8.5 · Room 2.6.1.

## Release assinada

Crie uma keystore fora do repositório e configure `signingConfigs` lendo caminho/senhas de variáveis de ambiente
ou de um `keystore.properties` (já no `.gitignore`). Nunca versione chaves privadas.
