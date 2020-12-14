CC=gcc
CFLAGS=-Wall -g -O2 -fomit-frame-pointer -fno-common -fno-builtin -fno-strict-aliasing
INCLUDES=-Iinclude/linux -Iinclude
LFLAGS=-shared -fPIC
DST=../dist/lib

all:
	${CC} ${CFLAGS} ${INCLUDES} ${LFLAGS} -o ${DST}/libtuntap.so org_idea_net_TunTap.c

clean:
	rm *.so
