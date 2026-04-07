# Repository Guidelines

## Project Structure & Module Organization
The repository centers on `docker-compose.yaml`, which defines the GPU-enabled `ollama` and `open-webui` services plus the persistent `open-webui` volume. Keep service-specific configs beside this file (for example, drop model presets under `configs/` and media under `assets/`), and mount them explicitly in the compose file so contributors know where data originates. Use additional folders only when they mirror a mounted path; orphan directories tend to drift out of sync with containers.

## Build, Test, and Development Commands
- `docker compose pull` fetches the pinned runtime images; run it before branching to ensure everyone works from the same base.
- `docker compose up -d` starts the stack with GPU reservations; confirm the host has the matching NVIDIA driver before invoking this command.
- `docker compose logs -f open-webui` streams backend output for debugging WebUI issues, while `docker compose logs ollama` surfaces model download or quantization problems.
- `docker compose down -v` is reserved for clean resets when the volume becomes polluted; warn the team before wiping shared chat history.

## Coding Style & Naming Conventions
Stick to two-space indentation and lowercase service keys in YAML. Environment variables remain UPPER_SNAKE_CASE and should be grouped logically (API, audio, GPU). When adding compose comments, keep them on their own line prefixed with `#` so they survive linting by `yamllint`.

## Testing Guidelines
After edits, run `docker compose config` to validate syntax, then `docker compose ps` to confirm both containers are healthy. Validate functional paths manually: `curl http://localhost:3000` should return the Open WebUI HTML, and `curl http://localhost:11434/api/tags` should list locally available Ollama models. For feature work that touches prompt flows, capture screenshots of the UI change and attach them to the pull request.

## Commit & Pull Request Guidelines
Use short, imperative commit subjects (e.g., `Add whisper defaults`). Describe significant compose or env changes in the body, outlining required driver versions and env variables. Pull requests should summarize the motivation, list any new ports or volumes, reference related issues, and link to verification evidence (logs, curls, or UI snippets) so reviewers can reproduce the scenario quickly.

## Security & Configuration Tips
Never hard-code credentials inside `docker-compose.yaml`; instead, rely on `.env` overrides referenced via `${VAR}` syntax and share templates under `configs/.env.example`. Review GPU permissions before sharing compose files externally, and scrub logs of private prompts before attaching them to tickets.