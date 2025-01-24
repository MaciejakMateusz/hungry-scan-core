# HungryScan - Spring Boot 3.x.x REST-API

Central server REST-API to handle HTTP communication from

- **CMS app:** https://github.com/MaciejakMateusz/hungry-scan-cms
- **Customer mobile app:** https://github.com/MaciejakMateusz/hungry-scan-customer

## Technologies Used

- **Java 21**
- **Spring Boot 3.x.x**
- **Spring Security 6.x.x**
- **Spring Data JPA**
- **Maven 4**
- **MySQL 8.3.0-1** - as database in docker container
- **Redis 7.4.1** - as cache in docker container
- **H2** - embedded database in test environment
- **com.google.zxing** - qr code generating library
- **mapstruct** - Entity->DTO mapping

## Functionalities:

- Handling HTTP requests, responses
- Validating incoming data
- Caching requests using Redis to supercharge response speeds 10x and reduce database connections
- Mapping received DTO's to entities and vice versa
- Communicating with other servers like mailing, AI models

## Project design

This REST-API is designed in data separation in mind. No session is stored, it is completely
stateless. Cache is stored separately using Redis. DTO's are used to hide real entities
structures and optimize responses, also preventing from infinite recursions.
The code is tested using Junit 5 and MockMvc (there are almost 450 tests).

## License

This project is licensed under the Read-Only License - Version 1.0.

### Read-Only License - Version 1.0

1. You are allowed to view and inspect the source code of this project for educational
   and non-commercial purposes.
2. You are not permitted to modify, distribute, sublicense, or use the source code or
   any part of it for any commercial purposes without explicit written permission from
   the project's author.
3. The project's author (Mateusz Maciejak) reserves all rights not expressly granted
   under this license.
4. This project is provided "as-is" and without any warranty. The project's author
   shall not be liable for any damages or liabilities arising from the use of the project.
5. The project's author retains all copyright and intellectual property rights to
   the source code.

For any questions or inquiries regarding commercial use, please contact the project's
author at maciejak.praca@gmail.com.

### Author

*Mateusz Maciejak*
