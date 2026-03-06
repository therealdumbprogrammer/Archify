# Archify

Archify helps you generate starter backend projects from a guided UI.

## Archify UI

Open the deployed app: `https://archify-brown.vercel.app`

For local development setup, see [LOCAL_RUN.md](./LOCAL_RUN.md).

## Quick Start

1. Open Archify UI.
2. In **Step 1**, choose a recipe card (or switch to YAML Spec mode).
3. In **Step 2**, configure your architecture:
- Set service name
- Add/edit entities
- Add/edit fields and types
4. In **Step 3**, click **Generate Project**.
5. Your ZIP downloads automatically as `archify-project.zip`.

## YAML Mode (Optional)

1. In **Step 1**, select **YAML Spec**.
2. Paste your YAML configuration.
3. Click **Apply YAML And Continue**.
4. Continue in either:
- **Form Mode** for guided edits, or
- **YAML Mode** for direct YAML editing.

## Naming Guide

- `serviceName`: generated service/module name (example: `user-service`)
- `Entity`: domain object/table (example: `User`, `Order`)
- `Field`: property/column inside an entity (example: `email`, `createdAt`)
- `id` field is managed as `Long` by default
