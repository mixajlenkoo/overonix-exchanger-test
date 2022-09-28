Task description:

Implement an application for providing up-to-date currency exchange rates for input
currencies (for example USD/EUR). The application should consume exchange rates data
from the 3d party systems via http calls. The application should consume data from the 3d
party system, store request and response from this system in DB, and return currency
exchange rates.
You can use any service of this list :
- https://freecurrencyapi.net/
- https://exchangeratesapi.io/
- https://www.exchangerate-api.com/docs/free
  or any others at your discretion.
  Mandatory requirements
1. It should be a simple java application based on Spring Boot framework.
2. The application should use an In-Memory Database for storing exchange rates
   history
3. The application should have a REST API that allows to get data by criteria:
- get exchange rates for currencies
- get available currency codes
- get exchange rates history by criteria parameters: currency, 3d party system
  identifier, data range etc.
4. The result should be printed in JSON format.
5. Your code should be version controlled and publicly accessible for us to review
   (github/bit-bucket/gitlab/etc)
6. Test coverage: API tests, unit tests
   Nice to have
1. Implement consuming data from the 2 3d party systems, choose the best exchange
   rate (minimum price for buying or maximum for selling) and return it to the user.
2. Deploy application to Docker container (Dockerfile artifact).
3. Using application.yml in the project as a configuration source.
4. Using logging.