# Auction House

The program will take command from STDIN and print the output into a file called _commandResults.out_, and
the errors/exceptions into a file called _commandErrors.err_.

The GUI part of the product will run on the swing worker thread, but some parts of it will also start new
threads or run on other special threads. When the program is run, the main frame will open, and alongside it
the _thread panel_, and the _product deposit_ will open.

The thread panel will show active auctions, each time a command to create an auction or to join an active one,
will alter this panel. By altering, the thread of the panel will wait for the auction house to call a "notifyAll()"
to wake the thread and right after the panel will continue sleeping. The panel will acquire a lock on the panel
instance and release it on the call of "wait()". The client creating the auction or the one joining will acquire a
lock on the panel and notify it to wake up. So in the guy we will see something like "1/2 subscribed", "2/2 subscribed"
which will immediately change to "auction has started", and the separate frame of the auction will be visible.

The product deposit is a list that keeps track of all product that currently available in the store's deposit,
it will update everytime the administrator adds a new product (using the producer-consumer problem), or after the
broker deletes a product (also using the producer-consumer problem) when a product is sold and needs to be removed from
that list.

([[Test1]]+[[Test2]]+[[Test3]]+[[Test4]]+[[Test5]]+[[Test6]]+[[Test7]]+[[Test8]]+[[Test9]]+[[Test10]]+[[Test11]]+
[[Test12]])/12+[[Lab]]+([[Partial]]+5)/2+[[Tema1]]*1.5+[[Tema2]]*1.5+[[Bonus]]+[[ACM]]+[[Proiect]]*2+([[Examen]]+10)/3,

The auction frame will have 3 separate components: the bidding log, the clients and brokers list, and a button
which will start the actual bidding process. The bidding log will display messages for actions like: a broker asks
a client to place a bid, and he receives the bid from that person, or when the auction has ended and one of 2 things
will be displayed: the product has not been sold because the required price has not been met, or the winner of
the auction and his winning bid otherwise. The communication between the auction(implicitly the clients) and the
brokers is being performed with the help of **proxy design pattern** of the broker class, this will allow future
developers to change the logs to the gui without actually changing the broker class itself.On the right side of the
screen there will be a list with all the clients subscribed to that auction, and their associated brokers.
There is one hidden button in the auction frame that will only be visible after the auction has ended. This button
will close the auction frame window and remove the auction from the list of active auctions.

Coming back to the input, the class "AllCommands" holds an enumeration of all possible the commands our program can
parse. The command processing has been done using the **command design pattern**, each concrete command implementing
the abstract command interface. The program must start with the loading of the auction house characteristics:
loading products, the clients, the brokers and the administrators. Right after that clients ca begin placing bids and
creating/ joining auctions.

A client can have 2 states in the auction: passive or active. A passive client will have his bids taken care of by
an automated algorithm in our program, and the active one will have the choice to give a command to the STDIN with
his concrete bid. To realise this I have implemented a **strategy design pattern** which will allow us to choose from
a variety of different algorithms for clients to place bids. At the moment there are just 2 algorithms:
"SimpleRandomFromMinMax" will calculate a random bit for passive clients using their maximum possible bids as an
upper bound, and the auction minimum price as a lower bound (the auction's minimum price is initially 0 and updated
afterwards with the previous bidding step maximum), and "ConcreteBidFromCommand" which will receive a concrete integer
bid value from the STDIN and make the client bid that amount (for active clients). In the future, developers can add
other bidding algorithms to expand the project.

The products, clients and brokers data will be imported from json files, located in the root of the module, these
will parse the jsons and with the help of the **builder design patterns** implemented for these 3 types, they will
populate a list of products, clients and broker using only desired fields from these 3.

The auction house, and the starting window will be singletons since only one instance of those objects can exist at
a given time (implemented the **singleton design pattern**). Also, classes like "AuctionHouse" and "Client" will make
use of the **composite design pattern**, which will treat their collection of objects as singular objects, assuring
a better encapsulation (instead of retrieving a list of Products and calling "add()" to that list, we can implement
a method inside that class that will take care of that for us, the end user doesn't have to know about the list itself
to add a product to it).