# 🎬 Microsserviço de Eventos

Parte do [Projeto Leo Microservices](../ReadMe.md). Responsável pelo **catálogo de
sessões/eventos do cinema** e pelo **controle de lugares**, com **banco de dados
próprio (PostgreSQL)** e integração assíncrona via **Apache Kafka**.

## 🛠️ Stack

- Java 21 · Spring Boot 3.4.0 · Spring Cloud 2024.0.0 (Eureka Client)
- Spring Data JPA · PostgreSQL (Docker)
- Spring for Apache Kafka
- Porta: **8082**

## 🧩 Responsabilidades

1. **CRUD do catálogo de eventos** (filme/sessão, sala, data/hora, capacidade, preço).
2. **Controle de lugares** reagindo aos eventos do domínio:

| Tópico Kafka                | Ação no serviço de Eventos                                  |
|-----------------------------|-------------------------------------------------------------|
| `cinema.reserva.criada`     | Segura os lugares (cria um *hold* `PENDENTE`)               |
| `cinema.pagamento.aprovado` | Confirma a reserva (lugares vendidos → `CONFIRMADA`)        |
| `cinema.pagamento.recusado` | Devolve os lugares ao evento (`CANCELADA`)                  |

O processamento é **idempotente** por `idReserva`.

## 📡 API REST

| Método | Rota                         | Descrição                          |
|--------|------------------------------|------------------------------------|
| POST   | `/eventos`                   | Cria um evento/sessão              |
| GET    | `/eventos`                   | Lista todos os eventos             |
| GET    | `/eventos/{id}`              | Detalha um evento                  |
| GET    | `/eventos/{id}/disponibilidade` | Lugares disponíveis e status    |
| PUT    | `/eventos/{id}`              | Atualiza dados do evento           |
| DELETE | `/eventos/{id}`              | Remove um evento                   |

### Exemplo — criar evento
```bash
curl -X POST http://localhost:8082/eventos \
  -H "Content-Type: application/json" \
  -d '{
        "titulo": "Duna: Parte 3",
        "descricao": "Sessão dublada",
        "sala": "Sala 4 IMAX",
        "dataHoraSessao": "2026-07-01T20:00:00",
        "capacidadeTotal": 120,
        "preco": 39.90
      }'
```

## 📨 Contratos dos eventos consumidos

**`cinema.reserva.criada`** (publicado pelo serviço de reservas):
```json
{
  "idReserva": "RES-123",
  "idEvento": 1,
  "quantidadeLugares": 2,
  "idUsuario": "USER-99",
  "emailUsuario": "cliente@email.com"
}
```

**`cinema.pagamento.aprovado` / `cinema.pagamento.recusado`** (serviço de pagamento):
```json
{
  "idReserva": "RES-123",
  "idUsuario": "USER-99",
  "emailUsuario": "cliente@email.com",
  "cpfUser": "00000000000",
  "status": "aprovado",
  "mensagemErro": null,
  "idPagamento": "pay_abc"
}
```
> O serviço relaciona o pagamento ao *hold* de lugares pelo campo `idReserva`.
> Como os eventos vêm de outros serviços (pacotes diferentes), os cabeçalhos de
> tipo do Kafka são ignorados: o valor é lido como JSON e convertido para o tipo
> do parâmetro de cada `@KafkaListener` (ver `config/KafkaConsumerConfig`).

## ▶️ Como executar

### 1. Subir a infraestrutura (Kafka + PostgreSQL)
Na raiz do projeto:
```bash
docker-compose up -d
```

### 2. Subir o Eureka Server
(no projeto do registry, porta 8761)

### 3. Subir este serviço
```bash
cd microservicesEventos
mvn spring-boot:run
```

O serviço registra-se no Eureka como `MICROSERVICESEVENTOS` e cria as tabelas
(`eventos`, `reservas`) automaticamente no banco `eventosdb` (`ddl-auto=update`).

> Requer **JDK 21** (mesma exigência dos demais serviços do repositório).
