# NPM dependency tree

## Usage

Running the service:
`sbt run` 

Getting the dependency tree of a package:
```bash
./npm_tree_dep.sh http-errors 1.7.2
```

you should get a response similar to:
```
└── http-errors-1.7.2
    ├── depd-1.1.2
    ├── inherits-2.0.3
    ├── setprototypeof-1.1.1
    ├── statuses-1.5.0
    └── toidentifier-1.0.0
```
or
```$xslt
└── express-4.17.1
    ├── accepts-1.3.7
    │   ├── mime-types-2.1.24
    │   │   └── mime-db-1.40.0
    │   └── negotiator-0.6.2
    ├── array-flatten-1.1.1
    ├── body-parser-1.19.0
    │   ├── bytes-3.1.0
    │   ├── content-type-1.0.4
    │   ├── debug-2.6.9
    │   │   └── ms-2.0.0
    │   ├── depd-1.1.2
    │   ├── http-errors-1.7.2
    │   │   ├── depd-1.1.2
    │   │   ├── inherits-2.0.3
    │   │   ├── setprototypeof-1.1.1
    │   │   ├── statuses-latest
    │   │   └── toidentifier-1.0.0
    │   ├── iconv-lite-0.4.24
    │   │   └── safer-buffer-latest
    │   ├── on-finished-2.3.0
    │   │   └── ee-first-1.1.1
    │   ├── qs-6.7.0
    │   ├── raw-body-2.4.0
    │   │   ├── bytes-3.1.0
    │   │   ├── http-errors-1.7.2
    │   │   │   ├── depd-1.1.2
    │   │   │   ├── inherits-2.0.3
    │   │   │   ├── setprototypeof-1.1.1
    │   │   │   ├── statuses-latest
    │   │   │   └── toidentifier-1.0.0
    │   │   ├── iconv-lite-0.4.24
    │   │   │   └── safer-buffer-2.1.2
    │   │   └── unpipe-1.0.0
    │   └── type-is-1.6.17
    │       ├── media-typer-0.3.0
    │       └── mime-types-2.1.24
    │           └── mime-db-1.40.0
    ├── content-disposition-0.5.3
    │   └── safe-buffer-5.1.2
    ├── content-type-1.0.4
    ├── cookie-0.4.0
    ├── cookie-signature-1.0.6
    ├── debug-2.6.9
    │   └── ms-2.0.0
    ├── depd-1.1.2
    ├── encodeurl-1.0.2
    ├── escape-html-1.0.3
    ├── etag-1.8.1
    ├── finalhandler-1.1.2
    │   ├── debug-2.6.9
    │   │   └── ms-2.0.0
    │   ├── encodeurl-1.0.2
    │   ├── escape-html-1.0.3
    │   ├── on-finished-2.3.0
    │   │   └── ee-first-1.1.1
    │   ├── parseurl-1.3.3
    │   ├── statuses-1.5.0
    │   └── unpipe-1.0.0
    ├── fresh-0.5.2
    ├── merge-descriptors-1.0.1
    ├── methods-1.1.2
    ├── on-finished-2.3.0
    │   └── ee-first-1.1.1
    ├── parseurl-1.3.3
    ├── path-to-regexp-0.1.7
    ├── proxy-addr-2.0.5
    │   ├── forwarded-0.1.2
    │   └── ipaddr.js-1.9.0
    ├── qs-6.7.0
    ├── range-parser-1.2.1
    ├── safe-buffer-5.1.2
    ├── send-0.17.1
    │   ├── debug-2.6.9
    │   │   └── ms-2.0.0
    │   ├── depd-1.1.2
    │   ├── destroy-1.0.4
    │   ├── encodeurl-1.0.2
    │   ├── escape-html-1.0.3
    │   ├── etag-1.8.1
    │   ├── fresh-0.5.2
    │   ├── http-errors-1.7.2
    │   │   ├── depd-1.1.2
    │   │   ├── inherits-2.0.3
    │   │   ├── setprototypeof-1.1.1
    │   │   ├── statuses-latest
    │   │   └── toidentifier-1.0.0
    │   ├── mime-1.6.0
    │   ├── ms-2.1.1
    │   ├── on-finished-2.3.0
    │   │   └── ee-first-1.1.1
    │   ├── range-parser-1.2.1
    │   └── statuses-1.5.0
    ├── serve-static-1.14.1
    │   ├── encodeurl-1.0.2
    │   ├── escape-html-1.0.3
    │   ├── parseurl-1.3.3
    │   └── send-0.17.1
    │       ├── debug-2.6.9
    │       │   └── ms-2.0.0
    │       ├── depd-1.1.2
    │       ├── destroy-1.0.4
    │       ├── encodeurl-1.0.2
    │       ├── escape-html-1.0.3
    │       ├── etag-1.8.1
    │       ├── fresh-0.5.2
    │       ├── http-errors-1.7.2
    │       │   ├── depd-1.1.2
    │       │   ├── inherits-2.0.3
    │       │   ├── setprototypeof-1.1.1
    │       │   ├── statuses-latest
    │       │   └── toidentifier-1.0.0
    │       ├── mime-1.6.0
    │       ├── ms-2.1.1
    │       ├── on-finished-2.3.0
    │       │   └── ee-first-1.1.1
    │       ├── range-parser-1.2.1
    │       └── statuses-1.5.0
    ├── setprototypeof-1.1.1
    ├── statuses-1.5.0
    ├── type-is-1.6.18
    │   ├── media-typer-0.3.0
    │   └── mime-types-2.1.24
    │       └── mime-db-1.40.0
    ├── utils-merge-1.0.1
    └── vary-1.1.2
```

### Design

The service is designed around three components:

- `GraphStore` -> which is responsible for storing the already fetched packages
- `PackageRepository` -> a query mechanism on top of the `GraphStore`
- `HttpOut` -> external requests
- `Controller` -> the main logic that orchestrates the interactions across components

A `node` in the Graph is represented by `PackageVersion` which its dependencies are fetched and cached or not.

Whenever a incoming request hits the service, it checks in the `GraphStore` for the directed graph of the requested node.
If all the downstream dependencies are already fetched it returns the graph, otherwise it fetches in an eagerly manner all the 
un-fetched dependencies.


### Finagle and Flint
As the fetching the dependency tree is a process that takes some time and involves many request
i choose tho use Finagle as http client, and flint as the http server (which is based on top of finagle).

Given its async nature, it would fit this application well as it would benefit of 
fetching the dependencies async and in parallel. 

Unfortunately given the time constraint, I eagerly evaluated finagle's `Future` in the  `Controller.fetchHttp` method. 


### Further development
- [ ] Deal gracefully with Exceptions: Propagate the richer type all the way from `HttpOut` to the endpoint.
- [ ] Fetch dependencies in parallel
- [ ] `Controller.fetchDependencies` returning a future to the endpoint
- [ ] Add retry logic to the `HttpOut` in case of retriable errors.
- [ ] Improve relative versions, possibly fetching all versions from a package and deciding the best one
- [ ] Expire `latest` nodes, as new versions might be available

