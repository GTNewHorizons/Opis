import asyncore
import binascii
import socket


class ClientTest(asyncore.dispatcher):

	def __init__(self, host, port):
		asyncore.dispatcher.__init__(self)
		self.create_socket(socket.AF_INET, socket.SOCK_STREAM)
		self.connect( (host, port) )
		self.buffer = "\x00\x00\x00\x06\x01\x00\x00\x00\x0c\x00\x00"

	def handle_connect(self):
		print "handle_connect"

	def handle_close(self):
		print "handle_close"
		self.close()

	def handle_read(self):
		print "handle_read"
		print binascii.hexlify(self.recv(8192))

	def handle_write(self):
		sent = self.send(self.buffer)
		self.buffer = self.buffer[sent:]

	def writable(self):
		return (len(self.buffer) > 0)

client = ClientTest('localhost', 25566)
asyncore.loop()
