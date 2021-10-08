# Примеры кода для ШБР
Примеры кода из второй части лекции для Школы разработки бэкенда

## Темы
* Jooq. Подключение и исполнение запросов

----
## Usage
1. Перед запуском тестов надо развернуть PostgreSQL. 
Можно сделать это в Docker с помощью команды:
```sh
docker run -e POSTGRES_PASSWORD=password -d postgres
```
[Ссылка](https://hub.docker.com/_/postgres) на официальный образ PostgreSQL.

2. Подключиться к базе данных и вручную создать таблицу **USERS**. SQL скрипт лежит в папке [*src/main/resources*](https://github.com/senyast4745/sbr-examples-jooq/blob/master/src/main/resources/init.sql)
3. Сгенерировать java классы из базы данных с помощью maven плагина jOOQ. Можно это сделать из IntelliJ Idea, а можно из терминала с помощью команды:
```sh
mvn jooq-codegen:generate
```
**!WARN!** Maven должен стоять на вашей машине

4. Запукать тесты
