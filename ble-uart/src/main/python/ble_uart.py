import threading

# sudo pip3 install bluezero
from bluezero import adapter
from bluezero import peripheral
from bluezero import device

# constants
UART_SERVICE =      '6E400001-B5A3-F393-E0A9-E50E24DCCA9E'
RX_CHARACTERISTIC = '6E400002-B5A3-F393-E0A9-E50E24DCCA9E'
TX_CHARACTERISTIC = '6E400003-B5A3-F393-E0A9-E50E24DCCA9E'


tx_obj = None

def on_connect(ble_device: device.Device):
    print('connect', str(ble_device.address), flush=True)

def on_disconnect(adapter_address, device_address, flush=True):
    print('disconnect', device_address, flush=True)

def on_tx_notify(notifying, characteristic):
    global tx_obj

    if notifying:
        tx_obj = characteristic
    else:
        tx_obj = None

def on_rx_write(value, options):
    print('rx', bytes(value).decode('utf-8'), flush=True)

def update_tx(tx):
    global tx_obj

    if tx_obj:
        print('tx', tx, flush=True)
        tx_obj.set_value([ ord(i) for i in list(tx) ]) # string to int list

def forward():
    while True:
        tx = input()
        print('input', tx, flush=True)

        update_tx(tx)

def main(adapter_address):
    threading.Thread(target = forward).start() # fork a thread to wait user input from stdin

    ble_uart = peripheral.Peripheral(adapter_address, local_name='BLE UART')
    ble_uart.add_service(srv_id=1, uuid=UART_SERVICE, primary=True)
    ble_uart.add_characteristic(srv_id=1, chr_id=1, uuid=RX_CHARACTERISTIC,
                                value=[], notifying=False,
                                flags=['write', 'write-without-response'],
                                write_callback=on_rx_write,
                                read_callback=None,
                                notify_callback=None)
    ble_uart.add_characteristic(srv_id=1, chr_id=2, uuid=TX_CHARACTERISTIC,
                                value=[], notifying=False,
                                flags=['notify'],
                                notify_callback=on_tx_notify,
                                read_callback=None,
                                write_callback=None)

    ble_uart.on_connect = on_connect
    ble_uart.on_disconnect = on_disconnect

    ble_uart.publish()

if __name__ == '__main__':
    main(list(adapter.Adapter.available())[0].address)
