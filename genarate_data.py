import cv2
import os 
import csv
from_dir = 'original_data/'
to_dir = 'JPGdata/'


img = cv2.imread(from_dir + str(1) + '_Color.png')
cv2.imshow(img)
cv2.imwrite(to_dir+str(1)+'.jpg', img)