# -*- coding: utf-8 -*-
"""
Created on Sat May 23 12:13:30 2020

@author: Matt
"""
import csv
import random

from numpy.random import Generator, PCG64
import time as _time
from datetime import datetime, date, time, timedelta

epoch = datetime.utcfromtimestamp(0)
#microsecond_interval=\
millisecond_interval=100
dayCount=30
def main():
    with open('C:/Users/mathe/data/test_data_30_Days_10hz.csv', 'w', newline='') as csvfile:
    #with open('C:/Users/mathe/data/test.csv', 'w', newline='') as csvfile:
        tic = _time.perf_counter()

        maxRowCount = int(dayCount * 24 * 60 * 60*1000/millisecond_interval)
        # print ('creating records '  % maxRowCount)
        print(f"creating {maxRowCount:0.1f} rows")
        csvWriter = csv.writer(csvfile, delimiter=',',
                                quotechar='|', quoting=csv.QUOTE_MINIMAL)
        headerRow = []
        headerRow.append("time")
        for x in range(0, 16):
            headerRow.append("Sensor_" + str(x))

        csvWriter.writerow(headerRow)

        generators = [Generator(PCG64()), Generator(PCG64()), Generator(PCG64()), Generator(PCG64()),
                      Generator(PCG64()), Generator(PCG64()), Generator(PCG64()), Generator(PCG64()),
                      Generator(PCG64()), Generator(PCG64()), Generator(PCG64()), Generator(PCG64()),
                      Generator(PCG64()), Generator(PCG64()), Generator(PCG64()), Generator(PCG64())]

        dt = datetime(2020, 6, 1,0,0,0,0)

        # Initialize first row
        previousRow = [];
        for x in range(0, 16):
            previousRow.append(1.0)

        variance = 0.1
        for rowIndex in range(0, maxRowCount):
            rowOfData = []
            rowOfData.append(unix_time_millis(dt))
            b = dt + timedelta(milliseconds=millisecond_interval)
            dt = b

            for x in range(0, 16):
                if (bool(random.getrandbits(1))):
                    sign = 1
                else:
                    sign = -1
                y = previousRow[x] + variance * generators[x].uniform((x * 10) + 1, (x * 10 + 100)) * sign;
                previousRow[x] = y
                # data[x] = y;
                rowOfData.append(str(y))
                #rowOfData.append(str(generators[x].uniform((x * 10) + 1, (x * 10 + 100))))
            csvWriter.writerow(rowOfData)

        toc = _time.perf_counter()
        print(f"Took {toc - tic:0.4f} seconds")

def unix_time_millis(dt):
     return (dt - epoch).total_seconds()

if __name__ == "__main__":
    main()