# -*- coding: utf-8 -*-
"""
Created on Tue May 26 15:07:04 2020

@author: Matt
"""

import pandas as pd
import matplotlib.pyplot as plt
import time as _time
from datetime import datetime, date, time, timedelta

convert = lambda x: datetime.utcfromtimestamp(float(x) / 1e3)
data = pd.read_csv("C:/Users/mathe/data/test_data_FourDays_10hz.csv",parse_dates=['time'], date_parser=convert)

#data['time'] = pd.to_datetime(data['time'],units='s')
#data.columns = ["time","Sensor_1"]
print(data.head())
print(data.tail())
#print(data)
#print(f" {data.} rows")
data.plot(kind='line', x='time', y=['Sensor_3','Sensor_9'], marker='o', markersize=0.7)
#data['Sensor_2'].plot(kind='hist', bins=100)
plt.xlabel('Value of time')
plt.ylabel('Value of Sensors')
plt.show()