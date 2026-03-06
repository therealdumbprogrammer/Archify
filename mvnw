#!/bin/sh
set -eu

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_DIR="$BASE_DIR/.mvn/wrapper"
PROPERTIES_FILE="$WRAPPER_DIR/maven-wrapper.properties"

if [ ! -f "$PROPERTIES_FILE" ]; then
  echo "Missing $PROPERTIES_FILE" >&2
  exit 1
fi

distribution_url=$(grep '^distributionUrl=' "$PROPERTIES_FILE" | tail -n 1 | cut -d'=' -f2-)
if [ -z "${distribution_url}" ]; then
  echo "distributionUrl is not set in $PROPERTIES_FILE" >&2
  exit 1
fi

archive_name=$(basename "$distribution_url")
dist_name="$archive_name"
dist_name="${dist_name%.tar.gz}"
dist_name="${dist_name%.zip}"
dist_root="$WRAPPER_DIR/dists"
install_dir="$dist_root/$dist_name"
mvn_bin="$install_dir/bin/mvn"

if [ -x "$mvn_bin" ]; then
  exec "$mvn_bin" "$@"
fi

if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

mkdir -p "$dist_root"
archive_path="$dist_root/$archive_name"

if [ ! -f "$archive_path" ]; then
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$distribution_url" -o "$archive_path"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$distribution_url" -O "$archive_path"
  else
    echo "Neither curl nor wget is available to download Maven." >&2
    exit 1
  fi
fi

if [ ! -x "$mvn_bin" ]; then
  if [ "${archive_name%*.zip}" != "$archive_name" ]; then
    if ! command -v unzip >/dev/null 2>&1; then
      echo "unzip is required to extract $archive_name" >&2
      exit 1
    fi
    unzip -q -o "$archive_path" -d "$dist_root"
  else
    tar -xzf "$archive_path" -C "$dist_root"
  fi
fi

if [ ! -x "$mvn_bin" ]; then
  echo "Failed to install Maven from $distribution_url" >&2
  exit 1
fi

exec "$mvn_bin" "$@"
