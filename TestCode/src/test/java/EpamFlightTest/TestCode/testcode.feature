Feature: Best Flight 
#I want to search best flight based on lowest fare and short time.

Scenario Outline: user inputs source and destination 
	Given I am at flight homepage 
	When I enter source "<source>" 
	And I enter destination "<destination>" 
	When I click on search button 
	Then I will display the best flight 
	Examples: 
		| source 	 | destination|
		| Hyderabad  | Delhi |
		|Hyderabad   |Mumbai|