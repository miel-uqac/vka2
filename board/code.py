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

"""
LED configuration (just) for debug/test
"""
led_red = digitalio.DigitalInOut(board.LED_RED)
led_green = digitalio.DigitalInOut(board.LED_GREEN)
led_blue = digitalio.DigitalInOut(board.LED_BLUE)

led_red.direction = digitalio.Direction.OUTPUT
led_green.direction = digitalio.Direction.OUTPUT
led_blue.direction = digitalio.Direction.OUTPUT

"""
Main loop
"""

#IDENTIFIANTS SPECIFIQUES 
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
idBackspace = r"\b"
idUsLayout = "#$L0"
idFrLayout = "#$L1"


while True:
    while not ble.connected:
        pass

    while ble.connected:
        _str = input() # read serial communication (type and press ENTER or RETURN)
        
        if _str == "red":
            led_red.value = False
            led_green.value = True
            led_blue.value = True
            kl.write("Light is now red")

        elif _str == "green":
            led_red.value = True
            led_green.value = False
            led_blue.value = True
            kl.write("Light is now green")

        elif _str == "blue":
            led_red.value = True
            led_green.value = True
            led_blue.value = False
            kl.write("Light is now blue")

        elif _str == idControlC:
            k.press(Keycode.CONTROL, Keycode.C)
            k.release_all()

        elif _str == idControlV:
            k.press(Keycode.CONTROL, Keycode.V)
            k.release_all()

        elif _str == idBackspace:
            k.send(Keycode.BACKSPACE)

        elif _str == idControlX:
            k.press(Keycode.CONTROL,Keycode.X)
            k.release_all()
        
        elif _str == idControlA:
            k.press(Keycode.CONTROL,Keycode.A)
            k.release_all()

        elif _str == idControlZ:
            k.press(Keycode.CONTROL,Keycode.Z)
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

        elif _str == idUsLayout:
            kl = KeyboardLayoutUS(k)

        elif _str == idFrLayout:
            kl = KeyboardLayoutFR(k)
            
        else:
            kl.write(_str)

    ble.start_advertising(advertisement)