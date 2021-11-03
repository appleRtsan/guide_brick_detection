import tensorflow as tf
from tensorflow.python.client import device_lib
from keras import models
import matplotlib.pyplot as plt
import random
import numpy as np
import cv2
import csv

model = tf.keras.Sequential()
model.add(tf.keras.layers.Conv2D(32, kernel_size=(3, 3), activation='relu', input_shape=(240, 320, 1)))
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

model.load_weights("C:/Users/User/Desktop/guide_brick/guidebrick.h5")

img = cv2.imread('C:/Users/User/Desktop/guide_brick/JPGdata/65_Color.jpg', 0)
img = cv2.resize(img, (320, 240))
img = tf.keras.preprocessing.image.img_to_array(img)
img = 1 - np.asarray(img).astype(float)/255
# cv2.imshow('img',img)
# cv2.waitKey(0)
out = model.predict(np.expand_dims(img,0))
print(out)
cv2.waitKey(0)
