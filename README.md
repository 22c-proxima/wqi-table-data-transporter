# wqi-table-data-transporter

Thanks to https://hub.docker.com/r/aa8y/postgres-dataset/

mvn exec:java -Dexec.mainClass="com.world_quant.test.App" -Dexec.classpathScope=runtime -Dexec.args="--from_db='jdbc:postgresql://127.0.0.1:5433/world?user=postgres&password=postgres' --from_tables country,city --to_db='jdbc:postgresql://127.0.0.1:5433/world?user=postgres&password=postgres' --to_schema=backup"
