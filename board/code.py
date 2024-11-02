"""
Import related to BLE
"""
import adafruit_ble
from adafruit_ble.advertising import Advertisement
from adafruit_ble.advertising.standard import ProvideServicesAdvertisement
from adafruit_ble.services.standard.hid import HIDService
from adafruit_ble.services.standard.device_info import DeviceInfoService
from adafruit_hid.keyboard import Keyboard
from adafruit_hid.keyboard_layout_us import KeyboardLayoutUS
from adafruit_hid.keyboard_layout_fr import KeyboardLayoutFR
from adafruit_hid.keycode import Keycode
from adafruit_hid.mouse import Mouse

"""
Import for LED and I/O serial communication
"""
import board
import digitalio

"""
BLE and HID configuration (connecting keyboard via Bluetooth to the host device)
"""
hid = HIDService()
device_info = DeviceInfoService(software_revision=adafruit_ble.__version__, manufacturer="Adafruit Industries")
advertisement = ProvideServicesAdvertisement(hid)
advertisement.appearance = 961
scan_response = Advertisement()
scan_response.complete_name = "CircuitPython HID"

ble = adafruit_ble.BLERadio()
if not ble.connected:
    print("advertising")
    ble.start_advertising(advertisement, scan_response)
else:
    print("already connected")
    print(ble.connections)

k = Keyboard(hid.devices)
kl = KeyboardLayoutFR(k)
m = Mouse(hid.devices)

"""
LED configuration (just) for debug/test
"""
led_red = digitalio.DigitalInOut(board.LED_RED)
led_green = digitalio.DigitalInOut(board.LED_GREEN)
led_blue = digitalio.DigitalInOut(board.LED_BLUE)

led_red.direction = digitalio.Direction.OUTPUT
led_green.direction = digitalio.Direction.OUTPUT
led_blue.direction = digitalio.Direction.OUTPUT



#IDENTIFIANTS SPECIFIQUES 
#---MACROS---
idControlC = "#$001"
idControlV = "#$002"

#---CLAVIER---
idBackspace = r"\b"

#---SOURIS---
idMouseLeftClick = "#$M01"
idMouseRightClick = "#$M02"
idMouseMove = "#$M1" #Sera suivit de la distance
idMouseSlide = "#$M2" #Sera suivit de la direction (V = vertical ou H = horizontal) et de la distance 


def macroAction(_str):
    if _str == idControlC:
        k.press(Keycode.CONTROL, Keycode.C)
        k.release_all()

    elif _str == idControlV:
        k.press(Keycode.CONTROL, Keycode.V)
        k.release_all()

    else:
        return True

    return False

def mouseAction(_str):
    
    if _str == idMouseLeftClick:
        m.click(Mouse.LEFT_BUTTON)

    elif _str == idMouseRightClick:
        m.click(Mouse.RIGHT_BUTTON)
        
    elif _str.startswith(idMouseMove):
        deplacement = _str[len(idMouseMove):]

        
    elif _str.startswith(idMouseSlide):
        deplacement = _str[len(idMouseSlide):]
        
        if(deplacement.startswith("H")):
            deplacement = deplacement[1:]
            m.move(wheel=int(deplacement))

        #elif(deplacement.startswith("V")):salltest
        
    else:
        return True

    return False


"""
Main loop
"""

while True:
    while not ble.connected:
        pass

    while ble.connected:
        _str = input() # read serial communication (type and press ENTER or RETURN)
        
        envoie = True #permet l'envoie du str brut 
    
        if _str.startswith("#$0"):
            envoie = macroAction(_str)

        elif _str.startswith("#$M"):
            envoie = mouseAction(_str)
        
        elif _str == idBackspace:
            k.send(Keycode.BACKSPACE)
            envoie = False
        
        if envoie:
            kl.write(_str)

    ble.start_advertising(advertisement)