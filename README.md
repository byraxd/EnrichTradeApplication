How to run service:
  Prerequisites::
    Java 17+
    Maven for building the project.
    Redis: A running Redis instance for example from docker dekstop by command: docker run --name my-redis -d -p 6379:6379 redis (default configuration expects Redis at localhost:6379). Adjust the configuration if necessary.

  Build and Run:
    1) First step need to clone repository by following command:
        git clone <repository_url>
    2) After need to cd to project_directory by command:
        cd <repository_directory>
    3) Run the application by following command:
        mvn spring-boot:run

  How to use API:
    1)Upload Products:
      - EndPoint should be POST
      - file should contain .csv or .json . For csv should be header productId, productName
      - Example how to use cUrl: curl -F "file=@/path/to/products.csv" http://localhost:8080/api/v1/products/uploads
    2)enrich
      - EndPoint should be POST
      - file should contain .csv or .json . For csv should be header date,productId,currency,price
      - Example how to use cUrl: curl -F "file=@/path/to/trades.csv" http://localhost:8080/api/v1/enrich --output enriched_trades.csv

  Limitations of the code:
    1) Files should contain .csv or .json.

  Any ideas for improvement if there were more time available:
    1) Implement supporting for .xml files
    2) Implement multithreading for fast file parsing

  Implemented tasks:
    1) Basic task.
    2) Support for JSON & XML (but only for json)
    3) Reactive Data Streaming(Using Reactor's flux)

  Screenshots from test Classes:
    1)ProductServiceImplTest:
      ![image](https://github.com/user-attachments/assets/47d52afc-f546-4714-8aaa-03b508290db0)
    2)TradeServiceImplTest:
      ![image](https://github.com/user-attachments/assets/fca6f911-c2f9-491e-82f6-e65da5cff777)



    
      
