const { Web3 } = require('web3');
require('dotenv').config();

const web3 = new Web3('https://rpc.ssafy-blockchain.com');

const NFTContractABI = require('../contracts/NFTFrame.json');
const NFTContractAddress = process.env.NFT_CONTRACT_ADDRESS;
const NFTContract = new web3.eth.Contract(NFTContractABI.abi, NFTContractAddress);

const coinContractABI = require('../contracts/AhoraCoin.json');
const coinContractAddress = process.env.COIN_CONTRACT_ADDRESS;
const coinContract = new web3.eth.Contract(coinContractABI.abi, coinContractAddress);

const marketContractABI = require('../contracts/NFTMarket.json');
const marketContractAddress = process.env.MARKET_CONTRACT_ADDRESS;
const marketContract = new web3.eth.Contract(marketContractABI.abi, marketContractAddress);

const auctionContractABI = require('../contracts/NFTAuction.json');
const auctionContractAddress = process.env.AUCTION_CONTRACT_ADDRESS;
const auctionContract = new web3.eth.Contract(auctionContractABI.abi, auctionContractAddress);

const privateKey = process.env.ADMIN_PRIVATE_KEY;
const account = web3.eth.accounts.privateKeyToAccount(privateKey);
web3.eth.accounts.wallet.add(account);

module.exports = {
  web3,
  NFTContract,
  coinContract,
  marketContract,
  auctionContract,
  account
};