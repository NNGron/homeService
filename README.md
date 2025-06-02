# Home Services

### Project Overview  
Home Services is a web-based software system designed to facilitate the process of providing and receiving household services (such as cleaning, repairs, etc.). The platform supports three main roles:

1. Customers
2. Experts (service providers)
3. Admins

Each role has a specific set of permissions and capabilities.

This project is developed in Java 23 using a layered architecture and RESTful API principles with role-based access control (RBAC).

**Key Technical Features:**  
- Modular and layered design with Spring Boot
- Spring MVC – Web layer for handling RESTful endpoints  
- Complete API documentation via Swagger UI  
- Postman collection for API testing  
- JWT-based authentication and role-based access (RBAC)  
- Production-ready configuration  
- Scalable and maintainable architecture for larger systems

## Core Features  
### For Customers  
- Sign-up and login (with email verification)
- Browse available services(categories) and subcategories
- Place service orders with price, date, and address (via map input)
- View offers from experts
- Accept offers and make payments
- Pay via wallet or online card (with CAPTCHA verification)
- Submit ratings and comments after service
- Auto-deduction of expert’s rating by 5% per hour delay in service completion

### For Experts  
- Two-step registration (email verification + admin approval)
- Upload profile photo
- Add/remove expertise (subcategories)
- View relevant service orders
- Submit offer with price and estimated time
- Start and complete orders
- Receive 70% of the order amount to wallet
- View received ratings and comments per service

### For Admin  
- Manage users, orders, and transactions
- Approve/reject experts
- Filter, sort, and generate reports
- Uses Criteria API, pagination, and sorting mechanisms

## Technologies Used  
- Java 17
- Spring Boot 3.4.3
- Spring Web, Spring Security + JWT
- Hibernate, JPA, PostgreSQL, H2
- Spring Data JPA, Validation
- Spring Mail, Spring Cache + Caffeine
- Swagger UI (Springdoc OpenAPI)
- Lombok, Maven
- Postman

## API Endpoints  

### Authentication & User Management
| Method | Endpoint                                          | Description                                | Role         |
| ------ | ------------------------------------------------- | ----------------------------------------   | ------------ |
| POST   | `/v1/users/signup`                                | User signup (Customer / Expert / Admin)    | Public       |
| POST   | `/v1/users/login`                                 | Login and receive JWT token                | Public       |
| POST   | `/v1/users/filter-users`                          | Advanced user filtering                    | Admin        |
| POST   | `/v1/users/upload-image`                          | Upload expert profile image                | Expert       |
| PUT    | `/v1/users/approve-user?email=`                   | Approve expert account                     | Admin        |
| PUT    | `/v1/users/update_users`                          | Update user info                           | Customer / Expert |
| PUT    | `/v1/users/update-password?newPassword=`          | Change user password with validation rules | All Users         |
| PUT    | `/v1/users/add-expert-categories?categoryNames=`  | Add subcategories to expert                | Expert       |
| DELETE | `/v1/users/expert-category?email=&categoryNames=` | Remove subcategories from expert           | Expert/Admin |
| DELETE | `/v1/users/delete-user`                           | Delete user account                        | Admin             |
| GET    | `/v1/users/all-user`                              | List all users (with pagination/sorting)   | Admin        |
| GET    | `/v1/users/resend-verification?email=`            | Resend email verification link             | Public            |

### Category Management
| Method | Endpoint                              | Description                               | Role  |
| ------ | ------------------------------------- | ----------------------------------------- | ----- |
| POST   | `/v1/categories/create-main-category` | Create a new main category                | Admin |
| POST   | `/v1/categories/create-sub-category`  | Create a new subcategory                  | Admin |
| DELETE | `/v1/categories/delete-sub-category?subCategoryName=`   | Delete subcategory   | Admin |
| DELETE | `/v1/categories/delete-main-category?mainCategoryName=` | Delete main category | Admin |
| GET    | `/v1/categories/main-categories`      | List all main categories                  | All   |
| GET    | `/v1/categories/sub-categories`       | List all subcategories                    | All   |
| GET    | `/v1/categories/pageable`             | Get paginated list of categories          | All   |
| GET    | `/v1/categories/pageable-sort`        | Get paginated & sorted list of categories | All   |

### Order Management
| Method | Endpoint                   | Description                                            | Role            |
| ------ | -------------------------- | ------------------------------------------------------ | --------------- |
| POST   | `/v1/orders/create-order`  | Customer creates a new service order                   | Customer        |
| PUT    | `/v1/orders/update-status` | Update order status (start/complete/select offer/etc.) | Customer/Expert |
| PUT    | `/v1/orders/paid`          | Pay via wallet                                         | Customer        |
| PUT    | `/v1/orders/paid/online`   | Pay via bank card with CAPTCHA                         | Customer        |
| PUT    | `/v1/orders/update-order?orderId=`       | Update an existing order                | Customer       |
| DELETE | `/v1/orders/delete-order?orderNumber=`   | Delete an order                         | Customer/Admin |
| GET    | `/v1/orders/show-orders`   | Expert views matching orders                           | Expert          |
| GET    | `/v1/orders/orders_history`              | View order history                      | Customer       |
| GET    | `/v1/orders/working-date`                | Show available working dates            | All            |
| GET    | `/v1/orders/orders-status`               | Get order status list                   | All            |
| GET    | `/v1/orders/filter-comprehensive-orders` | Filter orders with pagination & sorting | Admin          |

### Offer Management
| Method | Endpoint                     | Description                              | Role     |
| ------ | ---------------------------- | ---------------------------------------- | -------- |
| POST   | `/v1/experts/create-offer`   | Expert submits an offer                  | Expert   |
| PUT    | `/v1/experts/update-offer?offerNumber=` | Update submitted offer | Expert |
| DELETE | `/v1/experts/delete-offer?offerNumber=` | Delete submitted offer | Expert |
| GET    | `/v1/customers/order-offers` | Customer views all offers on their order | Customer |

### Wallet & Payments
| Method | Endpoint                                  | Description                 | Role            |
| ------ | ----------------------------------------- | --------------------------- | --------------- |
| PUT    | `/v1/credits/add-balance?email=&balance=` | Admin adds balance to user  | Admin           |
| GET    | `/v1/credits/show-credit`                 | View current wallet balance | Customer/Expert |
| POST   | `/v1/users/add/bank-card`                 | Add a virtual bank card     | Customer        |

### CAPTCHA System
| Method | Endpoint               | Description                           | Role   |
| ------ | ------------------------------ | ------------------- | ------ |
| GET  | `/v1/captcha/verify?id=&code=` | Verify CAPTCHA code | Public |
| GET    | `/v1/captcha/generate` | Generate CAPTCHA (for secure payment) | Public |

### Reviews & Ratings
| Method | Endpoint                      | Description                            | Role     |
| ------ | ----------------------------- | -------------------------------------- | -------- |
| POST   | `/v1/reviews/comment`         | Submit rating and comment              | Customer |
| GET    | `/v1/reviews/expert-comments` | Expert views their reviews and ratings | Expert   |

### Admin Tools / Reports
| Method | Endpoint                                          | Description                           | Role  |
| ------ | ------------------------------------------------- | ------------------------------------- | ----- |
| POST   | `/v1/admin/add-expert-subcategories?expertEmail=` | Admin assigns subcategories to expert | Admin |
| GET  | `/v1/users/registrations` | Filter user registrations by date range | Admin |
| GET  | `/v1/users/users-summary` | Summary report on users                 | Admin |

  
## Setup & Run Instructions

### Clone the Repository  
- git clone https://github.com/seyedin/homeService.git
- cd homeService

### Configure Database  
- Update application.yml in src/main/resources/:
- spring:
- datasource:
- url: jdbc:postgresql://localhost:5432/home_services
- username: your_username
- password: your_password
- jpa:
- hibernate:
- ddl-auto: update

### Run the Application  
- ./mvnw spring-boot:run

### API Documentation & Testing  
- Swagger UI: http://localhost:8080/swagger-ui.html
- Postman Collection: docs/Postman_Collection.json
- Use JWT tokens for secure endpoints

### Contributors  
- 👤 seyedin – Developer

### License  
- This project is released under the MIT License.
