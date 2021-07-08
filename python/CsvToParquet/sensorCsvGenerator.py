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
destinationDirectory = 'C:/Users/mathe/data/day/'
def main():
    for x in range(0, 3):
        writeDataFiles("Sensor_" + str(x), 1)

    for x in range(3, 6):
        writeDataFiles("Sensor_" + str(x), 60)

    for x in range(6, 16):
        writeDataFiles("Sensor_" + str(x), 100)

def writeDataFiles(sensorName, dataRate):
    print(f"writeDataFiles( {destinationDirectory}{sensorName}.csv @ {dataRate:d} capture rate")

    maxRowCount = int(1 * 24 * 60 * 60 * 1000 / dataRate);
    currentRow = 0
    fileIndex = 0
    while currentRow<maxRowCount:
        fileName = destinationDirectory + "CCHMS_20200401_" + sensorName + "_" + str(fileIndex).zfill(2) + ".csv"
        with open(fileName, 'w', newline='') as csvfile:
            tic = _time.perf_counter()

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

            dt = datetime(2020, 4, 1)

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
    return (dt - epoch).total_seconds() * 1000.0

if __name__ == "__main__":
    main()