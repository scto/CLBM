#!/bin/bash

# Definition der Namen
OLD_NAME="Atick Faisa"
NEW_NAME="Thomas Schmid"

echo "Starte Namensänderung in allen Projektdateien..."
echo "Ersetze '$OLD_NAME' durch '$NEW_NAME'..."

# Findet alle Dateien, schließt aber den .git und .gradle Ordner aus
# Ersetzt den Namen in:
# .kt (Kotlin), .kts (Gradle Kotlin), .xml, .java, .properties, .txt, .md
find . -type f \( \
    -name "*.kt" -o \
    -name "*.kts" -o \
    -name "*.xml" -o \
    -name "*.java" -o \
    -name "*.properties" -o \
    -name "*.txt" -o \
    -name "*.md" \
    \) -not -path "*/.*" -not -path "*/build/*" | while read -r file; do
    
    # Prüfen, ob der alte Name in der Datei vorkommt
    if grep -q "$OLD_NAME" "$file"; then
        # sed -i führt die Änderung direkt in der Datei aus
        # Auf Android/Linux ist die Syntax: sed -i 's/alt/neu/g'
        sed -i "s/$OLD_NAME/$NEW_NAME/g" "$file"
        echo "✅ Bearbeitet: $file"
    fi
done

echo "---"
echo "Fertig! Alle Vorkommen wurden ersetzt."
