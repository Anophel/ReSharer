# ReSharer

## Description
ReSharer is a simple tool for resource sharing between your computers. The server-side application provides access to a virtual filesystem and provides an interface for custom running a custom job. The client-side application provides a viewer of server filesystem, possibility to download any shared file and start a custom job. The custom job's class files have to be available somewhere on a ReSharer server (codebase server) for the ReSharer server, which runs the job (worker server). 

{:toc}

## Table of contents
- <a href="#installation">Installation</a>
- <a href="#usage">Usage</a>
- <a href="#documentation">Documentation</a>
 
## Installation
 
### Prerequisites
- Java 11
- Maven 3.6 (Older may work too)

#### Step 1
Clone repository

#### Step 2
Run with command: `mvn clean javafx:run`

## Usage

### Server side

At first, we will set up our first ReSharer server.

#### Step 1

Select use as `Server`

#### Step 2a

Now you will see the initial virtual file system of your server. 
To share files/directories click on the button `Add files`/`Add directories`. 
Files and directories can be deleted by right-clicking on them a selecting delete option. These files and directories will be deleted **only** from the VFS, not from your disk.
To save the current setting of your virtual file system use button `Save VFS`.

#### Step 2b

You can load your VFS from a file if you have already prepared your VFS as described in step 2a.

#### Step 3

You can start sharing your VFS and provide computing time by clicking on `Toggle sharing`. Indicator, whether you are sharing, is in the top left corner.

#### Step 4

Do not forget to save your VFS after you run some custom jobs.

### Client side

We've prepared a running ReSharer server and new we will use it.

#### Step 1

Select use as `Client`

#### Step 2

Insert an address of a ReSharer server to the `Server IP` input box in the top left corner.

#### Step 3

Click on `Connect` button.

#### Step 4

Now you will see the root directory of a server if everything goes right.

#### Step 5a

You can download a file by double-clicking on the item in the table representing the file. Some files and directories do not have to be available at the moment. The indicator of availability is in the `Status` column.

#### Step 5b

You can run a custom job on a ReSharer server. An example of the custom job is [SimpleResharerJob.java](https://github.com/Anophel/ReSharer/blob/master/src/main/java/cz/anophel/resharer/rmi/runner/SimpleResharerJob.java). To start the job you need to click on the `Start remote job`. A form will pop up with details about the job. You need to fill in the URL of codebase server, remote classpath, job's main class and name of the output file. The output file will be located on the worker server in the directory `ResharerJobResults`.

### [Documentation](https://anophel.github.io/ReSharer/docs/)

Documentation was created using [Doxygen](https://www.doxygen.nl/index.html) with [Doxyfile](https://github.com/Anophel/ReSharer/blob/master/Doxyfile) and can be found [here](https://anophel.github.io/ReSharer/docs/)





