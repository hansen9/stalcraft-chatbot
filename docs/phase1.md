create a data fetcher to pull data from the github repository.

the fetcher have 2 phase, one to pull the GitHub Tree API and then second to do raw file fetches base on the path of the GitHub Tree API.
reason for 2 phase fetching: the datas are inside subdirectories and ordered by their categories(i.e. armors, weapons, containers, etc.)

the data ingestion is at startup via `ApplicationReadyEvent`