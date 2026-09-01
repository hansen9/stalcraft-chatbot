|Decision|Choice|Why|
|--------|------|---|
|Parser tests|Pure unit tests, real fixture file (`0r2g1.json`)|Deterministic parsing logic, no I/O - fixtures catch real-shape regressions that hand-written JSON wouldn't|
|Fetcher tests|WireMock|Need actual HTTP-layer fidelity(headers,status codes, pagination) that Mockito can't simulate|
|Service orchestration tests|Mockito + `ArgumentCaptor`|Orchestration logic doesn't need real HTTP - speed over fidelity here|