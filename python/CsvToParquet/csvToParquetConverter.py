
import pandas as pd
import pyarrow as pa
import pyarrow.parquet as pq

import time as _time
from datetime import datetime, date, time, timedelta

csv_file = 'C:/Users/mathe/data/test_data_FourDays_10hz.csv'
parquet_file = 'C:/Users/mathe/data/test_data_FourDays_10_2TIMEhz.parquet'
chunksize = 100_000

epoch = datetime.utcfromtimestamp(0)

def main():
    tic = _time.perf_counter()
    convert = lambda x: datetime.utcfromtimestamp(float(x) / 1e3)
    csv_stream = pd.read_csv(csv_file, chunksize=chunksize, low_memory=False, parse_dates=['time'], date_parser=convert)
    #csv_stream['time'] = pd.to_datetime(csv_stream['time'], unit='ms')
    #csv_stream.head()
    for i, chunk in enumerate(csv_stream):
        #print("Chunk", i)
        if i == 0:
            # Guess the schema of the CSV file from the first chunk
            parquet_schema = pa.Table.from_pandas(df=chunk).schema
            # Open a Parquet file for writing
            parquet_writer = pq.ParquetWriter(parquet_file, parquet_schema, compression='snappy')
        # Write CSV chunk to the parquet file
        table = pa.Table.from_pandas(chunk, schema=parquet_schema)
        parquet_writer.write_table(table)

    parquet_writer.close()

    toc = _time.perf_counter()
    print(f"Took {toc - tic:0.4f} seconds")

if __name__ == "__main__":
    main()