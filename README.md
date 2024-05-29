# Умный дом

[Описание проекта](https://docs.google.com/document/d/1__b_uh5oQRw3BwfNGpWdE74p9PUBp8e5TrvSNbQ20GE)

[Main Service](./mobile-backend-main) - основной микросервис, содержащий контроллеры для приема HTTP-запросов от клиента (мобильного приложения), производит регистрацию и авторизацию пользователей с помощью JWT, кеширует данные с помощью Redis.

[PSQL Service](./mobile-backend-psql) - микросервис для работы с СУБД PostgreSQL.

[Logger Service](./mobile-backend-logger) - микросервис для получения и обработки логов от других микросервисов.

[Test Service](./mobile-backend-tester) - микросервис для имитации одновременной работы большого количества клиентов с приложением.

[Main Service REST API](postman_collection.json) - коллекция Postman
