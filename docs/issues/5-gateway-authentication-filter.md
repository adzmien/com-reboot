# Gateway Authentication Filter

## Spec Reference

ISSUE-5 from `docs/specs/spec-reboot-uam.md`

## What to build

`reboot-gateway` global filter: validate JWT signature and expiry, check `jti` against the Redis blacklist via `TokenBlacklist`, inject `X-User-Id`, `X-User-Roles`, and `X-User-Permissions` headers into the forwarded request. Bypass filter for `POST /auth/login` and `POST /auth/refresh`.

## Acceptance Criteria

- [ ] A request with a valid, non-blacklisted JWT is forwarded upstream with `X-User-Id` and `X-User-Roles` headers populated
- [ ] A request with no `Authorization` header returns `401`
- [ ] A request with an expired JWT returns `401`
- [ ] A request with a tampered JWT signature returns `401`
- [ ] A request with a blacklisted `jti` (post-logout) returns `401`
- [ ] `POST /auth/login` is accessible without a JWT

## Blocked by

- #3
- #4
