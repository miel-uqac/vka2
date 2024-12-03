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
layout = "FR"

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
idControlX = "#$003"
idControlA = "#$004"
idControlZ = "#$005"
idControlY = "#$006"
idControlF = "#$007"
idControlH = "#$008"
idControlP = "#$009"
idControlB = "#$010"
idControlI = "#$011"
idControlD = "#$012"

#---CLAVIER---
idBackspace = r"\b"
idEnter = "#$E"
idUsLayout = "#$L0"
idFrLayout = "#$L1"

#---SOURIS---
idMouseLeftClick = "#$M01"
idMouseRightClick = "#$M02"
idMouseMove = "#$M1" #Sera suivit de la distance sous forme : "#$M1:x:y"
idMouseSlide = "#$M2" #Sera suivit de la direction (V = vertical ou H = horizontal) et de la direction "#$M2V-1", attention vertical n'est pas implementee
idMouseStartHold = "#$M31"
idMouseStopHold = "#$M32"



def macroAction(_str): #Liste des commandes liees aux macros 
    if _str == idControlC:
        k.press(Keycode.CONTROL, Keycode.C)
        k.release_all()

    elif _str == idControlV:
        k.press(Keycode.CONTROL, Keycode.V)
        k.release_all()

    elif _str == idControlX:
            k.press(Keycode.CONTROL,Keycode.X)
            k.release_all()
        
    elif _str == idControlA:
        if(layout == "US"):
            k.press(Keycode.CONTROL,Keycode.A)
        else:
            k.press(Keycode.CONTROL,Keycode.Q)
        k.release_all()

    elif _str == idControlZ:
        if(layout == "US"):
            k.press(Keycode.CONTROL,Keycode.Z)
        else:
            k.press(Keycode.CONTROL,Keycode.W)
        k.release_all()

    elif _str == idControlY:
        k.press(Keycode.CONTROL,Keycode.Y)
        k.release_all()

    elif _str == idControlF:
        k.press(Keycode.CONTROL,Keycode.F)
        k.release_all()

    elif _str == idControlH:
        k.press(Keycode.CONTROL,Keycode.H)
        k.release_all()

    elif _str == idControlP:
        k.press(Keycode.CONTROL,Keycode.P)
        k.release_all()

    elif _str == idControlB:
        k.press(Keycode.CONTROL,Keycode.B)
        k.release_all()

    elif _str == idControlI:
        k.press(Keycode.CONTROL,Keycode.I)
        k.release_all() 

    elif _str == idControlD:
        k.press(Keycode.CONTROL,Keycode.D)
        k.release_all()
        
    else:
        return True

    return False

def mouseAction(_str): #Liste des commandes liees a la souris 
    
    if _str == idMouseLeftClick:
        m.click(Mouse.LEFT_BUTTON)

    elif _str == idMouseRightClick:
        m.click(Mouse.RIGHT_BUTTON)
        
    elif _str.startswith(idMouseMove):
        strTab = _str.split(":")
        _x = strTab[1][:]
        _y = strTab[2][:]
        m.move(x=int(_x),y=int(_y))

    elif _str.startswith(idMouseSlide):
        strTab = _str.split(":")
        _direction = strTab[1][:]
        _wheel = strTab[2][:]
        if _direction =="H":
            m.move(wheel=int(_wheel))

        #elif(deplacement.startswith("V")): #TODO 
        
    elif _str == idMouseStartHold:
        m.press(Mouse.LEFT_BUTTON)

    elif _str == idMouseStopHold:
        m.release(Mouse.LEFT_BUTTON)

    else:
        return True

    return False
  
def layoutAction(_str):
    global kl,layout
    if _str == idUsLayout:
        kl = KeyboardLayoutUS(k)
        layout = "US"

    elif _str == idFrLayout:
        kl = KeyboardLayoutFR(k)
        layout = "FR"
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
        _str = input() # read serial communication
        
        envoie = True #permet l'envoie du str brut 
    
        if _str.startswith("#$0"):
            envoie = macroAction(_str)

        elif _str.startswith("#$M"):
            envoie = mouseAction(_str)
        
        elif _str == idBackspace:
            k.send(Keycode.BACKSPACE)
            envoie = False
        
        elif _str == idEnter:
            k.send(Keycode.ENTER)
            envoie = False
            
        elif _str.startswith("#$L"):
            envoie = layoutAction(_str)
        
        if envoie:
            try:
                kl.write(_str)
            except KeyError:
                pass

    ble.start_advertising(advertisement)#$E