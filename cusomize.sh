#!/bin/bash

# Definition der alten und neuen Strukturen
OLD_PKG="dev.atick"
NEW_PKG="com.scto.clbm"

OLD_PATH="dev/atick"
NEW_PATH="com/scto/clbm"

echo "🚀 Starte umfassende Umbenennung von $OLD_PKG zu $NEW_PKG..."

# 1. Textuelle Ersetzung in allen relevanten Dateien
# Inklusive build-logic/convention (da dort oft Plugins definiert sind)
echo "📝 Ersetze Package-Namen in Kotlin-Dateien, Skripten und XMLs..."
find . -type f \( -name "*.kt" -o -name "*.kts" -o -name "*.xml" -o -name "*.gradle" -o -name "*.properties" \) -print0 | xargs -0 sed -i "s/$OLD_PKG/$NEW_PKG/g"

# 2. Verschieben der Verzeichnisstruktur
echo "📂 Passe Ordnerstruktur an (inkl. build-logic)..."

# Diese Suche deckt src/*/kotlin, src/*/java sowie die build-logic Module ab
find . -type d -path "*/$OLD_PATH" | while read -r dir; do
    # Wir berechnen den Basis-Pfad vor 'dev/atick'
    base_dir=${dir%/$OLD_PATH}
    new_dir="$base_dir/$NEW_PATH"
    
    if [ "$dir" != "$new_dir" ]; then
        echo "Bewege $dir -> $new_dir"
        
        # Erstelle den neuen Pfad
        mkdir -p "$new_dir"
        
        # Verschiebe Inhalt und lösche alten Pfad
        cp -r "$dir"/* "$new_dir/" 2>/dev/null
        rm -rf "$dir"
        
        # Bereinige leere Eltern-Ordner (z.B. 'dev/' entfernen wenn leer)
        parent_dir=$(dirname "$dir")
        if [ -d "$parent_dir" ] && [ -z "$(ls -A "$parent_dir")" ]; then
            rm -rf "$parent_dir"
        fi
    fi
done

# 3. Gradle Clean
echo "🧹 Bereinige Build-Ordner..."
if [ -f "./gradlew" ]; then
    chmod +x gradlew
    bash ./gradlew clean
else
    echo "⚠️  gradlew nicht gefunden. Bitte manuell 'clean' ausführen."
fi

echo "✅ Fertig! Das Projekt wurde umstrukturiert und bereinigt."
