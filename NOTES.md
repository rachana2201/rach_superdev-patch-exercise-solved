# NOTES

## Summary of changes
(Detailed write-ups are in `handwritten/`.)
- **SQL precedence bug** (repo query, `db/queries`, Oracle package): `A AND B OR C AND D` meant archived tasks leaked into results and the status filter was ignored for title matches. Parenthesised the `OR`. Also added an `id` tie-breaker to `ORDER BY` and escaped `%`/`_` in user input.
- **Controller:** removed the `Thread.sleep` that slowed short/blank queries by up to 1s. Bad `status`/`page`/`pageSize` now return 400 instead of 500. Overflow-safe offset, `Locale.ROOT`, `println` replaced by SLF4J.
- **Frontend:** stale responses could overwrite newer ones (AbortController); `loading` stuck on after an error and `error` never cleared; page not reset when filters changed; added 300ms debounce; rows stay visible while loading; aria-labels.

## Assumptions
Archived tasks should never be listed. The status filter applies to title and description matches alike. Max page size 100.

## Deliberately not changed
- Pagination is still in memory (`subList`). Fine at ~50 rows. I did not rewrite it to `Pageable` without being able to test against the real stack, and I wanted a small diff.
- No auth, DTOs, `status` String→enum, CORS config, or committed tests.

## Biggest remaining risk
In-memory pagination plus `LIKE '%x%'` on `LOWER()` columns is a full scan that loads every match per request, so it degrades as rows grow. The H2 console and unauthenticated API are next.

## Tools used
Claude helped review the code, draft the fixes and write throwaway tests. I reproduced the SQL bug on the seed data, ran Vitest tests (fail before, pass after; not committed) and checked the controller logic against stubs. I understood and can explain each change.
