## DESCRIPTION

Android management client for NmapServer using Spring (for requests to server) and SQLite, implemented with a simple GUI.
Needs login credentials to enter NmapServer management, can also register those credentials with the server.
In case of no internet connection, places the queued requests to NmapServer in the SQLite database.


## WHAT IT CAN DO

- It logs into the NmapServer with valid credentials (or can register valid credentials) that are stored in the NmapServer database.
- It has multiple GUI tabs: 
		- Showing NmapClients connected to the server.
		- Sending new Nmap jobs to Nmapclients through the server or showing all jobs the server has sent to Nmapclients.
		- Showing results Nmapclients have sent to the server. 
- With the resend button, it forces sending all Nmap jobs meant for clients that have been stored in the local SQLite database, due to an internet outage.
- Persistent log in on the App, with an option to logout in the GUI.