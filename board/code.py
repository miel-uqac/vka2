# ===============================
# code.py — BLE HID pour Klavier
# ===============================
# Microcontrôleur = clavier + souris BLE
# Reçoit des TOKENS depuis l’app Android et tape/relâche les touches.

# ----- Imports -----
import adafruit_ble
from adafruit_ble.advertising import Advertisement
from adafruit_ble.advertising.standard import ProvideServicesAdvertisement
from adafruit_ble.services.standard.hid import HIDService
from adafruit_ble.services.standard.device_info import DeviceInfoService

from adafruit_hid.keyboard import Keyboard
from adafruit_hid.keyboard_layout_fr import KeyboardLayoutFR
from adafruit_hid.keyboard_layout_us import KeyboardLayoutUS
from adafruit_hid.keycode import Keycode
from adafruit_hid.mouse import Mouse

import supervisor
import sys
import time

# ----- BLE HID / pub -----
hid = HIDService()
device_info = DeviceInfoService(
    software_revision=adafruit_ble.__version__,
    manufacturer="Adafruit",
)

advert = ProvideServicesAdvertisement(hid)
advert.appearance = 961  # 961 = clavier HID
scan_resp = Advertisement()
scan_resp.complete_name = "CircuitPython HID"

ble = adafruit_ble.BLERadio()
if not ble.connected:
    try:
        ble.start_advertising(advert, scan_resp)
    except Exception:
        pass

# ----- Périphériques HID -----
k = Keyboard(hid.devices)       # clavier bas niveau (press/release)
kl = KeyboardLayoutFR(k)        # écriture texte (par défaut FR)
m = Mouse(hid.devices)
layout = "FR"

# ----- Tokens simples -----
TOK_ENTER     = "#$ENTER"
TOK_TAB       = "#$TAB"
TOK_SPACE     = "#$SPACE"
TOK_BACKSPACE = "#$BACKSPACE"
TOK_DELETE    = "#$DELETE"

# Modificateurs (maintien)
TOK_SHIFT_DOWN = "#$SHIFT_DOWN"; TOK_SHIFT_UP = "#$SHIFT_UP"
TOK_CTRL_DOWN  = "#$CTRL_DOWN";  TOK_CTRL_UP  = "#$CTRL_UP"
TOK_ALT_DOWN   = "#$ALT_DOWN";   TOK_ALT_UP   = "#$ALT_UP"
TOK_META_DOWN  = "#$META_DOWN";  TOK_META_UP  = "#$META_UP"

# Navigation
TOK_END  = "#$END"
TOK_HOME = "#$HOME"
TOK_PGUP = "#$PGUP"
TOK_PGDN = "#$PGDN"

# Flèches (tap)
ARROWS = {
    "#$ARROW_UP": Keycode.UP_ARROW,
    "#$ARROW_DOWN": Keycode.DOWN_ARROW,
    "#$ARROW_LEFT": Keycode.LEFT_ARROW,
    "#$ARROW_RIGHT": Keycode.RIGHT_ARROW,
}

# Flèches (maintien)
PREFIX_KEYDOWN = "#$KEYDOWN:"
PREFIX_KEYUP   = "#$KEYUP:"

# Raccourcis (Ctrl+*)
CTRL_MAP = {
    "#$001": (Keycode.CONTROL, Keycode.C),
    "#$002": (Keycode.CONTROL, Keycode.V),
    "#$003": (Keycode.CONTROL, Keycode.X),
    "#$005": (Keycode.CONTROL, Keycode.Z),
    "#$006": (Keycode.CONTROL, Keycode.Y),
    "#$007": (Keycode.CONTROL, Keycode.S),
    "#$013": (Keycode.CONTROL, Keycode.A),
}

# Layout
TOK_US = "#$L0"
TOK_FR = "#$L1"

# ----- États de maintien -----
held_mods = set()  # SHIFT/CTRL/ALT/GUI tenus
held_keys = set()  # touches tenues (flèches…)

def _press_current_mods():
    """Ré-applique les mods actifs avant un press/tape."""
    if held_mods:
        try:
            k.press(*tuple(held_mods))
        except Exception:
            pass

def _mod_down(mod):
    if mod not in held_mods:
        held_mods.add(mod)
        try:
            k.press(mod)
        except Exception:
            pass

def _mod_up(mod):
    if mod in held_mods:
        held_mods.remove(mod)
        try:
            k.release(mod)
        except Exception:
            pass

def _key_down(kc):
    if kc not in held_keys:
        _press_current_mods()
        try:
            k.press(kc)
            held_keys.add(kc)
        except Exception:
            pass

def _key_up(kc):
    if kc in held_keys:
        try:
            k.release(kc)
            held_keys.remove(kc)
        except Exception:
            pass

def _tap(*keys):
    """press+release rapide, en respectant les mods."""
    try:
        _press_current_mods()
        if keys:
            k.press(*keys)
            k.release(*keys)
        _press_current_mods()
    except Exception:
        pass

# ----- Actions par token -----
def _do_ctrl(tok):
    mapping = CTRL_MAP.get(tok)
    if not mapping:
        return False
    _tap(*mapping)
    return True

def _do_layout(tok):
    global kl, layout
    if tok == TOK_US:
        kl = KeyboardLayoutUS(k); layout = "US"; return True
    if tok == TOK_FR:
        kl = KeyboardLayoutFR(k); layout = "FR"; return True
    return False

def _do_special(tok):
    # Mods
    if tok == TOK_SHIFT_DOWN: _mod_down(Keycode.SHIFT); return True
    if tok == TOK_SHIFT_UP:   _mod_up(Keycode.SHIFT);   return True
    if tok == TOK_CTRL_DOWN:  _mod_down(Keycode.CONTROL); return True
    if tok == TOK_CTRL_UP:    _mod_up(Keycode.CONTROL);   return True
    if tok == TOK_ALT_DOWN:   _mod_down(Keycode.ALT);   return True
    if tok == TOK_ALT_UP:     _mod_up(Keycode.ALT);     return True
    if tok == TOK_META_DOWN:  _mod_down(Keycode.GUI);   return True
    if tok == TOK_META_UP:    _mod_up(Keycode.GUI);     return True

    # Édition
    if tok == TOK_BACKSPACE: _tap(Keycode.BACKSPACE); return True
    if tok == TOK_DELETE:    _tap(Keycode.DELETE);    return True
    if tok == TOK_TAB:       _tap(Keycode.TAB);       return True
    if tok == TOK_ENTER:     _tap(Keycode.ENTER);     return True
    if tok == TOK_SPACE:     _tap(Keycode.SPACE);     return True

    # Flèches (tap)
    if tok in ARROWS: _tap(ARROWS[tok]); return True

    # Navigation
    if tok == TOK_END:  _tap(Keycode.END);       return True
    if tok == TOK_HOME: _tap(Keycode.HOME);      return True
    if tok == TOK_PGUP: _tap(Keycode.PAGE_UP);   return True
    if tok == TOK_PGDN: _tap(Keycode.PAGE_DOWN); return True

    return False

# ----- Flèches maintenues -----
NAME_TO_KEYCODE = {
    "ARROW_UP": Keycode.UP_ARROW,
    "ARROW_DOWN": Keycode.DOWN_ARROW,
    "ARROW_LEFT": Keycode.LEFT_ARROW,
    "ARROW_RIGHT": Keycode.RIGHT_ARROW,
}

def _key_down_by_name(name):
    kc = NAME_TO_KEYCODE.get(name)
    if not kc: return False
    _key_down(kc); return True

def _key_up_by_name(name):
    kc = NAME_TO_KEYCODE.get(name)
    if not kc: return False
    _key_up(kc); return True

# ----- Écriture texte -----
def _write_text(s: str):
    if not s:
        return
    for ch in s:
        try:
            _press_current_mods()
            kl.write(ch)
            _press_current_mods()
        except Exception:
            pass

# ----- Lecture série (depuis Android) -----
_buf = ""

def _read_line():
    """Lit jusqu'à \n ; renvoie la ligne (sans \n) ou None."""
    global _buf
    try:
        while supervisor.runtime.serial_bytes_available:
            ch = sys.stdin.read(1)
            if ch is None:
                break
            if ch == "\r":
                continue
            if ch == "\n":
                line = _buf
                _buf = ""
                return line
            _buf += ch
    except Exception:
        return None
    return None


def _ensure_advertising():
    """Si déconnecté, relance la pub BLE."""
    if not ble.connected and not ble.advertising:
        try:
            ble.start_advertising(advert, scan_resp)
        except Exception:
            pass

print("Ready.")

# ----- Boucle principale -----
while True:
    _ensure_advertising()

    line = _read_line()
    if line is None:
        time.sleep(0.002)
        continue

    raw = line.strip()
    if not raw:
        time.sleep(0.001)
        continue

    t = raw.strip().upper()
    handled = False

    # 1) Ctrl+*
    if t.startswith("#$0"):
        handled = _do_ctrl(t)

    # 2) Layout
    if not handled and t.startswith("#$L"):
        handled = _do_layout(t)

    # 3) Flèches maintenues
    if not handled and t.startswith(PREFIX_KEYDOWN):
        handled = _key_down_by_name(t.split(":", 1)[1])
    if not handled and t.startswith(PREFIX_KEYUP):
        handled = _key_up_by_name(t.split(":", 1)[1])

    # 4) Spéciaux (mods, édition, flèches tap, nav)
    if not handled:
        handled = _do_special(t)

    # 5) Sinon = texte brut
    if not handled:
        _write_text(raw)

    time.sleep(0.001)
