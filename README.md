# Sber diplom
<h2 align="center">Инструкции к запуску</h2>
<h3>БД:</h3>
Для начала работы необходимо в своем пользователе postgresql создать две базы данных 
diplomdb (для работы приложения) и diplomdbtest (для тестовой среды).
<h3>Проект:</h3>
Клонировать репозиторий https://github.com/VeronikaOxyt/sber-diplom.git и внести следующие изменения: 
В директориях src/test/resources и src/main/resources в файлах application.yml и application-test.yml
внести свои логин и пароль пользователя postgresql

![image](https://github.com/VeronikaOxyt/sber-diplom/assets/132338823/ab5165a3-942d-4cc8-91ad-14e4c349cd72)

а также ввести пароль от smtp.yandex сервера рассылки сообщений который я дам вместе с ключом API для Яндекс-карт

![image](https://github.com/VeronikaOxyt/sber-diplom/assets/132338823/1f0b2e17-39ce-41d3-acf8-c82723779c3d)

Далее в html-документе src/main/resources/templates/poster.html ввести ключ API для Яндекс-карт

![image](https://github.com/VeronikaOxyt/sber-diplom/assets/132338823/66d36508-abdc-49be-b8a8-4cd75142462e)

При необходимости загружать фото в веб-приложение в application.yml
 можно указать путь до папки на вашем пк для их хранения

![image](https://github.com/VeronikaOxyt/sber-diplom/assets/132338823/a9f3b515-82c0-4f0e-b4dc-343caa16d6db)

<h3>Проект можно запускать</h3>
Автоматически создадутся два администратора с ролями ROLE_REFEREE и ROLE_INSTRUCTOR
Их данные для авторизации:

login: Referee
<hr>
password: 456

login: Instructor
<hr>
password: 789

При дальнейшей регистрации пользователей они будут создаваться только с ролью ROLE_USER
