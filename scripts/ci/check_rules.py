"""Contrôles mécaniques des règles du CLAUDE.md, joués par la CI.

Seules les règles vérifiables sans connaître la stack sont contrôlées ici ; les autres
relèvent de la relecture.
"""

import subprocess
import sys
from pathlib import Path

MAX_FILE_LINES = 300
CODE_SUFFIXES = {".java"}
GENERATED_MARKERS = ("/generated/",)
FORBIDDEN_FILES = (".pem", ".key", ".p12", ".jks", ".keystore")


def tracked_files() -> list[Path]:
    listing = subprocess.run(
        ["git", "ls-files"], capture_output=True, text=True, check=True
    )
    return [Path(line) for line in listing.stdout.splitlines()]


def is_generated(path: Path) -> bool:
    return any(marker in path.as_posix() for marker in GENERATED_MARKERS)


def check_file_size(path: Path) -> str | None:
    """[TAILLE-FICHIER] 300 lignes au plus par fichier Java."""
    if path.suffix not in CODE_SUFFIXES or is_generated(path):
        return None
    line_count = len(path.read_text(encoding="utf-8").splitlines())
    if line_count <= MAX_FILE_LINES:
        return None
    return f"{path}: {line_count} lignes (max {MAX_FILE_LINES}) [TAILLE-FICHIER]"


def check_secrets(path: Path) -> str | None:
    """[SECRETS] Aucun .env réel ni clé privée dans le dépôt."""
    name = path.name
    is_env_file = name.startswith(".env") and name != ".env.example"
    if is_env_file or name.endswith(FORBIDDEN_FILES):
        return f"{path}: fichier secret suivi par git [SECRETS]"
    return None


def main() -> int:
    violations = [
        violation
        for path in tracked_files()
        for violation in (check_file_size(path), check_secrets(path))
        if violation
    ]
    for violation in violations:
        print(f"::error::{violation}")
    if violations:
        return 1
    print("Règles du CLAUDE.md respectées.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
