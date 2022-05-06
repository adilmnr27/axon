# axon
A project for self learning purpose based on udemy course



# HTTP Requests Example
1) To Insert a Product

POST
http://localhost:8082/products-service/products (Routing via G/W)
{
    "title": "colgate2",
    "price": 600,
    "quantity": 3
}

2) To fetch Products

GET
http://localhost:8082/products-service/products

3) Create new order

POST
http://localhost:8082/orders-service/orders
//product id could be found from fetch products
{
    "productId": "f284e519-e255-4ea3-9e65-97d9e819bda8", 
    "addressId": "addressId1",
    "quantity": 1
}

4) Replay Events

GET
http://localhost:8082/products-service/replay/{processName}/reset

Example: http://localhost:8082/products-service/replay/product-group/reset


5) h2-console

GET
http://localhost:8082/products-service/h2-console

username: root
password: root

GET
http://localhost:8082/orders-service/h2-console

username: root
password: root



#Replaying Events
While replaying events make sure to change the event processor to tracking