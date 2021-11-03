import tensorflow as tf
from tensorflow.python.client import device_lib
from keras import models
import random
import numpy as np
import cv2 
import csv

encoding = '12345'
labels_file = 'C:/Users/User/Desktop/guide_brick/labels.csv'
# gpu = tf.config.experimental.list_physical_devices('GPU')
# tf.config.experimental.set_memory_growth(gpu[0], True)

img_list = []
label_list = []

with open(labels_file, newline='') as csvfile:
    reader = csv.DictReader(csvfile)
    fieldnames = reader.fieldnames
    for row in reader:
        img_list.append(row[fieldnames[0]])
        label_list.append(encoding.index(row[fieldnames[1]]))
    
items=[]
for i in range(len(img_list)):
    items.append(i)
X = []
y = []
for i in random.sample(items,len(img_list)):
    
    img = cv2.imread(img_list[i], 0)
    img = cv2.resize(img, (640, 480))
    # print(img_list[i])
    # cv2.imshow('img',img)
    # cv2.waitKey(0)
    img = tf.keras.preprocessing.image.img_to_array(img)
    X.append(img)
    y.append(label_list[i])

X = 1 - np.asarray(X).astype(float)/255 # invert and scale
y = np.array(y).astype(float)

model = tf.keras.Sequential()
model.add(tf.keras.layers.Conv2D(32, kernel_size=(3, 3), activation='relu', input_shape=(480, 640, 1)))
model.add(tf.keras.layers.BatchNormalization())
model.add(tf.keras.layers.MaxPooling2D(pool_size=(2, 2)))
model.add(tf.keras.layers.Conv2D(64, (3, 3), activation='relu'))
model.add(tf.keras.layers.BatchNormalization())
model.add(tf.keras.layers.MaxPooling2D(pool_size=(2, 2)))
model.add(tf.keras.layers.Conv2D(128, (3, 3), activation='relu'))
model.add(tf.keras.layers.BatchNormalization())
model.add(tf.keras.layers.Flatten())
model.add(tf.keras.layers.Dropout(rate=0.25))
model.add(tf.keras.layers.Dense(128, activation='relu'))
model.add(tf.keras.layers.Dropout(rate=0.5))
model.add(tf.keras.layers.Dense(5, activation='softmax'))
print(model.summary())

initial_learning_rate = 1e-3
lr_schedule = tf.keras.optimizers.schedules.ExponentialDecay(
    initial_learning_rate,
    decay_steps=100000,
    decay_rate=0.9,
)
model.compile(
    loss=tf.keras.losses.SparseCategoricalCrossentropy(), 
    optimizer=tf.keras.optimizers.Adam(learning_rate=lr_schedule), 
    metrics=[tf.keras.metrics.SparseCategoricalAccuracy()]
)
batch_size = 2
epochs = 80

model.fit(
    X, 
    y,
    batch_size = batch_size,
    epochs = epochs,
    validation_split=0.2,
    callbacks = [tf.keras.callbacks.EarlyStopping(monitor='val_loss', patience=5)]
)
# print(tf.test.is_gpu_available())
# print(device_lib.list_local_devices())