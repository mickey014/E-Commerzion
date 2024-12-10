
## User Service

Register User - POST Method
```
http://localhost:8001/api/v1/users/register
```
```
{
    "name": "Kirk Mendoza",
    "username": "kirk006",
    "password": "test",
    "confirmPassword": "test"
}

```

Login User - POST Method
```
http://localhost:8001/api/v1/users/login
```
```
{
    "username": "kirk006",
    "password": "test"
}
```

Update User - PUT Method
```
http://localhost:8001/api/v1/users/{id}
```
```
{
    "name": "Kirk Mendoza",
    "username": "kirk006",
    "email": "kirkmendoza9@gmail.com",
    "password": "test",
    "photoUrl": "https://kirk.dev.com",
    "status": "ACTIVE"
}
```

Get User - GET Method
```
http://localhost:8001/api/v1/users/{id}
```

Safe Delete User - PUT Method
```
http://localhost:8001/api/v1/users/safe/delete/{id}
```

Force Delete User - DELETE Method
```
http://localhost:8001/api/v1/users/force/delete/{id}
```

## Seller Service

Register Seller - POST Method
```
http://localhost:8101/api/v1/sellers/register
```
```
{
    "userId": 1,
    "sellerName": "Kirk Mendoza",
    "storeName": "K Tech Shop",
    "email": "kirkmendoza9@gmail.com",
    "phoneNumber": "00000000000",
    "location": "Kirk St.",
    "photoUrl": "https://kirk.dev.com"
}
```

Update Seller - PUT Method
```
http://localhost:8101/api/v1/sellers/user/{id}/seller/{id}
```
```
{
    "sellerName": "Kirk Mendoza test",
    "storeName": "K Tech Shop test",
    "email": "kirkmendoza9@gmail.com",
    "phoneNumber": "00000000000",
    "location": "Kirk St.",
    "photoUrl": "http://test.com",
    "status": "ACTIVE"
}
```

Get Seller - GET Method
```
http://localhost:8101/api/v1/sellers/user/{id}/seller/{id}
```

Safe Delete Seller - PUT Method
```
http://localhost:8101/api/v1/sellers/user/{id}/seller/{id}
```

Force Delete Seller - DELETE Method
```
http://localhost:8101/api/v1/sellers/user/{id}/seller/{id}
```

## Product Service

Create New Product - POST Method
```
http://localhost:8101/api/v1/products
```
```
{
    "sellerId": 1,
    "userId": 1,
    "productName": "Iphone 15",
    "skuCode": "20241116iphone15",
    "productDescription": "Iphone Description",
    "productCategory": "Mobile Phones",
    "price": 99.95,
    "features": "",
    "stock": 100,
    "availability": "In Stock",
    "photoUrl": ""
}
```

Product Purchase - POST Method
```
http://localhost:8101/api/v1/products/purchase
```
```
{
    "customerId": 1,
        "productId": 1,
        "orderStatus": "Pending",
        "shippingAddress": "1234 Elm Street, Some City, Some Country",
        "paymentMethod": "Credit Card",
        "paymentStatus": "Paid",
        "shippingMethod": "Standard",
        "orderItems": [
            {
                "quantity": 2, 
                "unitPrice": 50.25
            },
            {
                "quantity": 1,
                "unitPrice": 150.25
            }
        ]
}
```

Update Product - PUT Method
```
http://localhost:8101/api/v1/products/user/{id}/seller{id}/product/{id}
```
```
{
    "sellerId": 1,
    "userId": 1,
    "productName": "Iphone 15",
    "skuCode": "20241116iphone15",
    "productDescription": "Iphone Description",
    "productCategory": "Mobile Phones",
    "price": 99.95,
    "features": "",
    "stock": 100,
    "availability": "In Stock",
    "photoUrl": ""
}
```

Show Product - GET Method 
```
http://localhost:8201/api/v1/products
```

Show Product By Seller - GET Method 
```
http://localhost:8201/api/v1/products/seller/{id}
```

Show Product By Seller and Product - GET Method 
```
http://localhost:8201/api/v1/products/seller/{id}/product/{id}
```

Force Delete Product - DELETE Method 
```
http://localhost:8201/api/v1/products/force/delete/user/{id}/seller/{id}/product/{id}
```

## Order Service

Get Order - GET Method 
```
http://localhost:8301/api/v1/orders/{id}
```

Get Order By Customer - GET Method 
```
http://localhost:8301/api/v1/orders/customer/{id}/order/{id}
```
```
http://localhost:8301/api/v1/orders/customer/{id}
```

Update Order Status - PUT Method 
```
http://localhost:8301/api/v1/orders/customer/{id}/order/{id}
```
```
{
    "orderStatus": "Shipped",
    "shippingAddress": "Montalban Rizal",
    "paymentMethod": "COD",
    "paymentStatus": "PAID"
}
```

## Order Items Service

Get Order Items By Customer - GET Method 
```
http://localhost:8401/api/v1/orderItems/customer/{id}
```

Get Order Items - GET Method 
```
http://localhost:8401/api/v1/orderItems/{id}
```