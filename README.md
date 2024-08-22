## A short utility to clone multiple git-repositories into specified destination directory

### 1. Build executable jar-file

To create executable jar-file just invoke the applied [./build.sh](build.sh) script:

```bash
./build.sh
```

You will get executable jar-file named `GitCloner.jar` in the generated `build/compiled` directory.

### 2. Clone multiple repositories

Let's have amount of repositories paths listed in file `/home/user/DesirableGitRepos.txt`:

```text
ssh://git@git.myrepo.net/repo1.git
ssh://git@git.myrepo.net/repo2.git
etc...
```

These repositories supposed to be cloned into the directory `/home/user/DestinationFolder` by the following invocation:

```bash 
$ java -jar GitCloner.jar /home/user/DesirableGitRepos.txt /home/user/DestinationFolder
===> Run command with arguments:
        - sourcePath: /home/user/DesirableGitRepos.txt;
        - destinationPath: /home/user/DestinationFolder
===> Cloning into 'repo1'...
===> Cloning into 'repo2'...
etc...
```

After such processing the first letter of every successfully processed row of file `/home/user/DesirableGitRepos.txt` will be replaced
with `+` sign:

```text
+sh://git@git.myrepo.net/repo1.git
+sh://git@git.myrepo.net/repo2.git
etc...
```

The presence of `+` sign allows not to process successfully treated paths again in case of consequent invocations (next invocation raises
error for such row).
