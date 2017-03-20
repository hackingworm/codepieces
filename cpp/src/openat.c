#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

int main(int argc, char **argv) {
	#if 0
	if (2 > argc) {
		fprintf(stderr, "Usage: %s file\n", argv[0]);
		return -1;
	}

	if (-1 == openat(AT_FDCWD, argv[1], O_RDONLY)) {
		perror(NULL);
		return -1;
	}
	#else
	if (3 > argc) {
		fprintf(stderr, "Usage: %s file1 file2\n", argv[0]);
		return -1;
	}

	int fd;
	if (-1 == (fd = openat(AT_FDCWD, argv[2], O_RDONLY))) {
		perror(NULL);
		return -1;
	}

	struct file_handle handle;
	int mount_id;

	handle.handle_bytes = 0;
	name_to_handle_at(AT_FDCWD, argv[1], &handle, &mount_id, 0);
	if (EOVERFLOW != errno) {
		perror(NULL);
		return -1;
	}

	printf("Size of struct file_handle %ld/%ld, handle_bytes %d\n",
		sizeof(struct file_handle), sizeof(handle), handle.handle_bytes);

	struct file_handle *hp;
	if (NULL == (hp = malloc(sizeof(struct file_handle) + handle.handle_bytes))) {
		perror(NULL);
		return -1;
	}

	hp->handle_bytes = handle.handle_bytes;
	if (-1 ==  name_to_handle_at(AT_FDCWD, argv[1], hp, &mount_id, 0)) {
		perror(NULL);
		return -1;
	}

	printf("mount_id %d, handle_bytes %d\n", mount_id, hp->handle_bytes);

	#if 0
	if (-1 == open_by_handle_at(AT_FDCWD, hp, O_RDONLY)) {
	#else
	if (-1 == open_by_handle_at(fd, hp, O_RDONLY)) {
	#endif
		free(hp);
		perror(NULL);
		return -1;
	}

	free(hp);
	#endif

	printf("Succeeded in opening %s\n", argv[1]);
	return 0;
}
