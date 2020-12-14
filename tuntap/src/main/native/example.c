
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <linux/ioctl.h>
#include <linux/if.h>
#include <linux/if_tun.h>
#include <errno.h>
#include <unistd.h>
#include <sys/ioctl.h>

#define MTU 1500

// sudo ip tuntap add dev tap0 mode tap
// sudo ifconfig tap0 172.0.0.1 up

int main() {
  int fd;
  struct ifreq ifr;
  const char* tap = "tap0";
  char bytes[MTU];

  if ((fd = open("/dev/net/tun", O_RDWR)) < 0) {
    perror("Failed to open /dev/net/tun");
    return fd;
  } 

  memset(&ifr, 0, sizeof(ifr));
  memcpy(ifr.ifr_name, tap, strlen(tap));

  ifr.ifr_flags = IFF_TAP | IFF_NO_PI;  // TAP
  if (ioctl(fd, TUNSETIFF, (void*) &ifr) < 0) {    
    perror("Failed to open tap");
    close(fd);
    return -1;
  }

  for (;;) {
    fd_set set;
    int timeout = 1000; // time-out for select()
    struct timeval to;
    int s;

    FD_ZERO(&set);
    FD_SET(fd, &set);

    to.tv_sec = timeout / 1000;
    to.tv_usec = (timeout % 1000) * 1000;

    if (select(fd + 1, &set, NULL, NULL, &to) <= 0) { // signal from given file descriptor
      printf("No more packet\n");
      continue;
    }

    s = read(fd, bytes, MTU);

    for (int i = 0;i < s;i++) {
      printf("%02X ", bytes[i] & 0x0FF);
    }   

    printf("\n");
  }
}
