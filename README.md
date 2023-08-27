# SQL-To-Mongo-Translator

<img src="https://i.ibb.co/kDm0kJq/Translator.png" alt="Screenshot" width="100%">

## Table of Contents

- [Introduction](#introduction)
- [Overview](#overview)
- [Patterns Used](#patterns-used)
- [Supported SQL Clauses](#supported-sql-clauses)
- [Usage](#usage)
- [Configuration](#configuration)
- [Dependencies](#dependencies)

## Introduction

The SQL to MongoDB Query Translator is a project from "Databases" course at Faculty of Computing in Belgrade. It let users to convert SQL queries into MongoDB queries.

## Overview

The project follows a structured process to convert SQL query to MongoDB query:

### Parsing SQL Queries
- User inputs an SQL query as string.
- The parser slices the query string and creates a query object containing a list of clauses.
- Each clause contains information about its tables.

### Validation
- The query object is sent to the validator for syntax validation.

### Translation Process
- After validation, the query object is sent to adapter for translation.
- The adapter converts the SQL query object into a MongoDB query object.
- Parameters are extracted from the SQL query and mapped to MongoDB aggregation template.
- The aggregation template is packed into a list of documents, ready to be sent for processing to the MongoDB database.

## Patterns Used

- Adapter
- Factory Method
- Observer
- Singleton

## Supported SQL Clauses

The SQL to Mongo Translator supports the following SQL clauses:

- SELECT
- DISTINCT
- FROM
- WHERE (including AND, OR, LIKE, BETWEEN, IN, IS NULL, IS NOT NULL)
- Aggregation functions (MAX, MIN, AVG, COUNT)
- HAVING
- GROUP BY
- JOIN
- USING / ON
- ORDER BY
- Up to one subquery

## Usage

- When using the IN clause, utilize square brackets `[` `]`, without any spaces within the brackets.
- For aggregation functions, use the format: `FUNCTION(COLUMN)`.
- For USING or ON, avoid using parentheses.
- Enclose subqueries within curly braces `{` `}`.
- Do not use aliases.
- Avoid new lines and use only single spaces for separation.

## Configuration

Configure the Constants class in the project:

- Set the `MONGO_URI` constant to your MongoDB connection URI.
- Set the `DATABASE_NAME` constant to the name of your MongoDB database.

## Dependencies
- mongodb-driver-sync v4.10.2
