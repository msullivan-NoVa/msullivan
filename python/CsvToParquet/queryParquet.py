
import pyarrow.parquet as pq
import matplotlib.pyplot as plt
import time as _time

parquet_file = 'C:/Users/mathe/data/test_data_FourDays_10_TIMEhz.parquet'
chunksize = 100_000

def main():
    tic = _time.perf_counter()
    table = pq.read_table(parquet_file, columns=['time','Sensor_3', 'Sensor_9'])
    #table = pq.read_table(parquet_file)
    toc = _time.perf_counter()
    print(f"Took {toc - tic:0.4f} seconds")
    print(f" {table.num_rows} rows")
    series = table.to_pandas()
    #table['Sensor_3'].plot(kind='hist', bins=100)

    df_sample= series.sample(1000000);
    #df_sample.plot(kind='line', x='time', y='Sensor_3', marker='o', markersize=0.7)
    df_sample.plot(kind='line', x='time', y=['Sensor_3','Sensor_9'], marker='o', markersize=0.7)
    #series.plot(kind='line', x='time', y='Sensor_3', data=df_sample, marker='o', markersize=0.7)
    #df_sample = df.sample(1000)


    plt.xlabel('Value of time')
    plt.ylabel('Value of Sensors')
    plt.show()

if __name__ == "__main__":
    main()