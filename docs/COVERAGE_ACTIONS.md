Coverage Actions — Postpaid Billing System

Current snapshot (from backend/target/site/jacoco/index.html)
- Overall instruction coverage: 80% (605 missed instructions of 3,088)
- Branch coverage: 54% (50 missed branches of 110)
- Packages flagged for improvement:
  - `com.abc.postpaid.customer.service.impl` — 51%
  - `com.abc.postpaid.user.service.impl` — 69%
  - `com.abc.postpaid.config` — 71%

Action plan
1. Add unit tests for service implementations
   - `InvoiceServiceImpl`, `PaymentServiceImpl`, `UsageRecordServiceImpl` (already added some tests)
   - `CustomerServiceImpl` and related customer services — add happy/error paths
   - `AdminUserServiceImpl` — add create/get/update/delete/password tests
2. Improve branch coverage by adding negative tests (not found, invalid inputs) and conditional branches (e.g., default status null handling).
3. Re-run tests and iterate until desired threshold reached. Suggested target: 80%+ overall and 70%+ branch coverage.

Recommended test structure
- Use JUnit 5 and Mockito for service tests (mock repositories)
- Keep tests isolated and fast; avoid integration DB usage for unit tests
- Add a small set of integration tests to exercise mapping and controller-security wiring

Commands
```powershell
cd C:\workspace\postpaid-billing-system\backend
mvn -DskipTests=false test jacoco:report
# open report
start target\site\jacoco\index.html
```

If you want, I can start adding the `CustomerServiceImpl` unit tests now and run the coverage report to measure improvement.
