## E-Commerzion Project

## Docker commands

	Run The Production Engine
	```
	docker compose -f docker-compose.prod.yml up -d
	```
  
	Stop The Production Engine
	```
	docker compose -f docker-compose.prod.yml down
  ```
	
	Run The Development Engine
	```
	docker compose up -d 
	```
  
	Stop The Development Engine
	```
	docker compose down
	```
  rabbit mq bug if orders and order items service down 
	the rabbit mq itself keeps looping


	troubleshoot
	- delete the queue to fix. 