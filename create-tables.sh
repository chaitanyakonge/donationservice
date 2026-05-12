#!/bin/bash

# ─────────────────────────────────────────────
# Create ngo-donors table
# ─────────────────────────────────────────────
aws dynamodb create-table \
  --table-name ngo-donors \
  --attribute-definitions \
    AttributeName=PK,AttributeType=S \
    AttributeName=contactNumber,AttributeType=S \
    AttributeName=fullName,AttributeType=S \
    AttributeName=createdAt,AttributeType=N \
  --key-schema \
    AttributeName=PK,KeyType=HASH \
  --global-secondary-indexes \
    '[
      {
        "IndexName": "contactNumber-index",
        "KeySchema": [
          {"AttributeName": "contactNumber", "KeyType": "HASH"},
          {"AttributeName": "createdAt", "KeyType": "RANGE"}
        ],
        "Projection": {"ProjectionType": "ALL"},
        "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5}
      },
      {
        "IndexName": "fullName-index",
        "KeySchema": [
          {"AttributeName": "fullName", "KeyType": "HASH"},
          {"AttributeName": "createdAt", "KeyType": "RANGE"}
        ],
        "Projection": {"ProjectionType": "ALL"},
        "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5}
      }
    ]' \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --region ap-south-1

echo "ngo-donors table creation initiated..."

# ─────────────────────────────────────────────
# Create ngo-donations table
# ─────────────────────────────────────────────
aws dynamodb create-table \
  --table-name ngo-donations \
  --attribute-definitions \
    AttributeName=PK,AttributeType=S \
    AttributeName=donorId,AttributeType=S \
    AttributeName=monthPartition,AttributeType=S \
    AttributeName=transactionEpoch,AttributeType=N \
  --key-schema \
    AttributeName=PK,KeyType=HASH \
  --global-secondary-indexes \
    '[
      {
        "IndexName": "donorId-index",
        "KeySchema": [
          {"AttributeName": "donorId", "KeyType": "HASH"},
          {"AttributeName": "transactionEpoch", "KeyType": "RANGE"}
        ],
        "Projection": {
          "ProjectionType": "INCLUDE",
          "NonKeyAttributes": ["amount", "paymentMode", "transactionEpoch", "status", "eventDescription"]
        },
        "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5}
      },
      {
        "IndexName": "monthPartition-index",
        "KeySchema": [
          {"AttributeName": "monthPartition", "KeyType": "HASH"},
          {"AttributeName": "transactionEpoch", "KeyType": "RANGE"}
        ],
        "Projection": {
          "ProjectionType": "INCLUDE",
          "NonKeyAttributes": ["amount", "paymentMode", "status"]
        },
        "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5}
      }
    ]' \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --region ap-south-1

echo "ngo-donations table creation initiated..."
echo "Run 'aws dynamodb list-tables --region ap-south-1' to verify."
