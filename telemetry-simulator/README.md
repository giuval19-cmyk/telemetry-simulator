# 🛸 Real-time Drone Telemetry Simulator

A production-ready Spring Boot microservice designed to simulate, manage, and control a distributed fleet of drones in real-time. It integrates REST APIs, a reactive-ready processing engine, **Spring Cloud Eureka** for service discovery, and secure messaging via **CloudAMQP (RabbitMQ)** supporting SSL.

## 🚀 Key Features

* **Fleet Management (REST API)**: Launch a complete drone fleet loaded from configuration files, monitor status, and stop operations dynamically.
* **Telemetry Producer**: Periodically streams real-time drone telemetry events (position, status, battery levels) to a dedicated RabbitMQ exchange.
* **Command Consumer**: Listens to an operational command queue to intercept and execute real-time commands like `recall` or `relocate` on active drone instances.
* **Cloud-Native Ready**: Fully integrated with Netflix Eureka for service registry and externalized secure cloud messaging.

---

## 🛠️ Tech Stack

* **Java 21**
* **Spring Boot** 
* **Spring Cloud Netflix Eureka Client**
* **CloudAMQP** 
* **Maven**

## 🚀 Getting Started

### Prerequisites
* Java Development Kit (JDK 21)
* An active Eureka Discovery Server instance running (or configure connection accordingly)
* A RabbitMQ / CloudAMQP instance