About Formatting Messages:
tags shown are case-sensitive, and can be added & removed to customize what each message shows
some global tags that can be used in any message:
	<r> 	will attempt to right-align preceeding text
	<l> 	will attempt to left-align preceeding text
	<c> 	will attempt to center (all) preceeding text
	<r>,<l>, & <c> will accept replacement chars: <r.> will right-align using . as spacer
also, colors can use shorthand '&' tags or full tags as such:
	long tag	| short |	data	
	<black>			&B		&0
	<darkblue>		&N		&1	('N' for 'Navy')
	<darkgreen>		&G		&2
	<darkaqua>		&Q		&3
	<darkred>		&R		&4
	<darkpurple>	&P		&5
	<gold>			&U		&6	('U', as in 'AU')
	<gray>			&d		&7	('d' for 'dark')
	<darkgray>		&D		&8
	<blue>			&b		&9
	<green>			&g		&a
	<aqua>			&q		&b
	<red>			&r		&c
	<lightpurple>	&p		&d
	<yellow>		&y		&e
	<white>			&w		&f
	<magic>			&~		&k	(unreadable text in minecraft - randomly changing letters)
	<bold>			<b>		&l
	<strike>		<del>	&m
	<underline>		<u>		&n	(dosn't play well with multiline - put a blank line underneath to make easier to read)
	<italic>		<em>	&o
	<reset>			<r>		&r	(reset all formatting)
	<newline>		<br>	&\	(begin a new line of text)
	<endcolor>		</>		&/	(end tag - returns to last used format or color (can be nested))
    <rainbow>       &^      &z  (special option - each letter in the text will be a different bright text color (randomizes for each use))
	&& is used if you want an '&' symbol, and don't want it to turn the next character into a color code
========= Extended Node Tags: =========

Commands.Prefix:		what comes before each and EVERY BetterShop message. (Putting default colors here makes things easy)
Commands.Unknown_Item:	if someone tries to check an item that has no definition
	<item> - what was looked up
Commands.Bad_Parameter:	something went wrong parsing the command (missing arguments, or in wrong order?)
	<error>	- hopefully useful informatino on what caused the error

Shop.No_Last_Sale: if a player runs '/sellagain' and has not sold anything since reload
Shop.No_Last_Buy: if a player runs '/buyagain' and has not bought anything since reload
Shop.Sell_Again: message shown to player when repeating sale
	<action> - the arguments that are being run again (items being sold)
Shop.Buy_Again:  message shown to player when repeating purchase
	<action> - the arguments that are being run again (items being bought)

ShopList.Head:  top of a shop listing page (blank for none (not recommended))
	<page> - current list page
	<pages> - total # of pages
ShopList.Listing:  price listing message
	<item> - what was looked up
	<buyprice> - current buy cost
	<sellprice> - current sell value
	<curr> - currency name
	<avail> - current avaliable stock (if enabled)
ShopList.Tail:  bottom of market list page (blank for none)
ShopList.Alias:  what is shown if a user looks up an item's aliases
	<item> - what was looked up
	<alias> - comma-delimited string of aliases
ShopList.Nolist:  message that the item is neither for sale nor can be sold.
	<item> - what user is looking up
ShopList.Pricecheck:  pricecheck is what shows up when a player asks for a item lookup
	<item> - what was looked up
	<buyprice> - current buy cost
	<sellprice> - current sell value
	<curr> - currency name
	<max> - how much they can buy
	<buycur> - formatted buy cost (like "1,800 Coins" instead of "1800 Coin")
	<sellcur> - formatted sell value
	<avail> - current avaliable stock (if enabled)

Permission.Denied: permission denied message. 
	<perm> can be used to show the permission node that was checked.

Items: Default Item Names for each item id, with acceptable aliases (comma-delimited)
	item id keys are in the following format: ID [-DATA] [_sub]
	the _sub key is to allow for data items to be accessed using "main value":"sub value" (eg. "wool:black")
	item names may be colored, but only one color per item is accepted
Potions: how potions are named
  Extra-Time: the name an extended duration potion is given
	<name> is to specify where in the name the rest of the potion name is put (defaults to end of the string)
  Splash-Potion: name of a splash potion
	this is used instead of 'Potion-Name' when a potion is a splash potion
  Potion-Name: the name to look for in the potion name to replace with the splash potion string