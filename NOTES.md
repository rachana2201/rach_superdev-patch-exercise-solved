# NOTES

## Summary of changes

(Detailed explanations are in the `handwritten/` folder.)

* **Search bug:** The SQL search used `AND` and `OR` without proper brackets. Because of this, archived tasks could appear and the status filter could be ignored in some searches. I added brackets to fix the search logic. I also handled special characters such as `%` and `_` and added `id` as a second sorting value.
* **Backend:** Removed an unnecessary 1-second delay. Added validation for `status`, `page`, and `pageSize` so invalid values return a `400` error instead of a `500` error. I also made the page offset calculation safer and replaced `println` with proper logging.
* **Frontend:** Fixed an issue where an older search result could replace a newer result. I used `AbortController` to cancel the older request. Added a 300ms delay before searching, fixed loading and error states, reset the page when filters change, and added better accessibility labels.

## Assumptions

* Archived tasks should never be shown.
* The status filter should work for both title and description searches.
* The maximum page size is 100.

## Deliberately not changed

* Pagination still happens in memory because the current data is small. I did not make a bigger database change without testing the complete application.
* I did not add authentication, DTOs, enums, CORS changes, or new committed tests.

## Biggest remaining risk

The search can become slower when there are many tasks because it searches through the data using `LIKE`. The H2 console and unauthenticated API would also need more attention for a production application.

## Tools used

I used ChatGPT to review the code, understand the bugs, suggest fixes, and create temporary tests. I tested the SQL problem with the seed data and checked the frontend and backend changes. I understood the changes I made and can explain them.
