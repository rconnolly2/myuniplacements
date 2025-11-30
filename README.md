# Zelora - Concurrent Programming Assignment

This is my Concurrent Programming Assignment 3 project based on the Zelora e-commerce system. For this project I used Spring Boot along with the Thymeleaf templating engine. FontAwesome is included for icons and TailwindCSS is used for styling. The goal of the assignment was to extend the original Zelora project with additional functionality such as product searching, filtering, product pages, a shopping cart system, account management, and a referrals feature. This repository contains my full implementation of those requirements.

---

## Home Page
![Home](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/home.png?raw=true)

---

## How to Set Up the Project

### 1. Database Setup

I modified and added several tables from the original assignment due to the need for auto-increment fields and additional structures. Two new tables were added:

- `addresses`
- `referrals`

To prepare the database:

1. Locate the SQL dump file:
   ```
   static/sql/new_zelora_db.sql
   ```
2. Import this SQL file into your MySQL database.
3. Update your database credentials in:
   ```
   src/main/resources/application.properties
   ```

---

### 2. Frontend Setup

The frontend does not require installing Tailwind or FontAwesome. Both are included through CDN links inside the `<head>` of the templates. The styling and icons will work out of the box.

---

### 3. Email Sender Configuration (Java Mail)

I added my own email credentials for testing the Java Mail sender.  
You can review or update these details inside:

```
application.properties
```

---

### 4. Recommended First Login

For testing the application with populated data, log in using this customer account:

Email: `eoin.murphy1@hotmail.com`  
Password: `robert`

---

## Introduction

For the concurrent programming assignment, we were provided a base Zelora shop template. My focus was to redesign it with a cleaner, more intuitive layout and apply a green color scheme to align with sustainable branding.

The main functionalities implemented are:

- Full search functionality
- Advanced filtering
- DataTables integration
- Product page features
- Shopping cart system
- User account pages
- Referrals system
- Wishlist and address management

![Package diagram](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/package-diagram.png?raw=true)

---

## Search Functionality

The application provides a search feature that works by product keywords, name, description, or material.

A filtering form is available on the left side of the product results page, allowing filtering by:

- Manufacturer
- Material
- Category
- Minimum price
- Maximum price
- Release date

Almost all views across the site use DataTables.

![Search](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/search.png?raw=true)

---

## Product Page

The product page is designed to be simple and informative. It includes:

- Average review score
- Individual reviews
- Form to write a new review
- Product thumbnail
- Current stock information

Stock is calculated using lambda expressions.

Available actions on this page:

- Add to Wishlist
- Add to Shopping Cart

![Product](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/product.png?raw=true)

---

## Shopping Cart

The shopping cart functionality is intentionally straightforward.

- The cart is stored in the session as a `List<Product>`.
- The route `/cart/add/{productId}` adds a product.
- During checkout:
    - An Order is created.
    - Order Items are added.
    - The session list is cleared.

Referral logic:

- First order gets 10% discount (if referral exists)
- Discount applies once
- Referral status becomes `USED`

![Cart](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/cart.png?raw=true)

---

## My Account

The My Account section contains six main subpages:

![Profile](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/profile.png?raw=true)

### 1. Profile

![Profile](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/edit-profile.png?raw=true)

### 2. Wishlist

![Wishlist](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/wishlist.png?raw=true)

### 3. My Orders

![Orders](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/orders.png?raw=true)

The orders page supports filtering using:
```
/orders?status=delivered
```

### 4. Addresses

![Address](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/addresses.png?raw=true)

### 5. Referrals (Advanced Feature)

![Referral](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/referrals.png?raw=true)

---

## Advanced Feature: Referrals System

The referrals system allows users to:

- Invite new customers by email
- Send a random six-digit referral code
- Track referral status
- Receive a 10% discount when the referred user places their first order

![Referral sequence](https://github.com/limerickIT/assignment-three-concurrent-programming-rconnolly2/blob/main/src/main/resources/static/images/readme/referral-sequence.png?raw=true)

---

# Conclusion

Overall, I’m pretty happy with the end result. This was my first time using Spring Boot, so I had to do a fair amount of research, but it wasn’t too difficult since I already have experience with frameworks like Flask, Django, and CodeIgniter 4 that follow a similar Model Template Controller structure. I also learned how to use DataTables during this project, and once I understood how it worked, it was very straightforward to set up. 
