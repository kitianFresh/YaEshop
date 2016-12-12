# J2EE environment setup on ubuntu16.04

## Tomcat8.0.37
### Install via apt
```
// Update
sudo apt-get update && apt-get upgrade
 
// Install tomcat packages
sudo apt-get install tomcat8( default is tomcat 8.0)

// Optional
sudo apt-get install tomcat8-docs tomcat8-admin tomcat8-examples tomcat8-user
 
// Make sure you have defined java environment varibale JAVA_HOME

// To start/stop/reatart
sudo service tomcat8 start/stop/restart

// If success, you can access tomcat via http://localhost:8080
 
```
&emsp;&emsp;以上命令对我的ubuntu16.04尽然失败了。。。

### Install Manually
#### Step 1: Download
&emsp;&emsp;可以去[官网](http://tomcat.apache.org)下载tomcat8.0.37，选择Binary distribution => Core => Download 来下载"tar.gz"格式的包;也可以使用命令下载8.0
```
wget http://apache.fayea.com/tomcat/tomcat-8/v8.0.37/bin/apache-tomcat-8.0.37.tar.gz
```

#### Step 2: Install
&emsp;&emsp;首先解压并创建软链接
```
cd ~/Download # enter tomcat package dir
sudo tar -xzf apache-tomcat-8.0.37.tar.gz -C /opt  # extract archieve to directory /opt

// 这里创建一个软链接到apache-tomcat-8.0.37的目的就是方便以后更换tomcat版本，只用更换软链接的指向的实际tomcat目录即可，就是更改一下软链接
// 另外，注意/opt/tomcat8后面不要带'/'
sudo ln -s /opt/apache-tomcat-8.0.37/ /opt/tomcat8 # create link for conveniece
```


&emsp;&emsp;下面添加tomcat8用户是为了安全考虑，tomcat8由tomcat8用户和组运行，其他人没有权限；并更改软链接和目录的owner；
```
sudo useradd -s /sbin/nologin tomcat8                 # add user to run tomcat service
sudo chown -R tomcat8:tomcat8 /opt/tomcat8            # and give it ownership
sudo chown -R tomcat8:tomcat8 /opt/tomcat8/           # to tomcat's directory
```


&emsp;&emsp;然后创建系统服务，这一部分可以选择不要。在/etc/init.d/下面新建一个tomcat8文件，将以下代码拷贝过去；
```
#!/bin/sh
### BEGIN INIT INFO
# Provides:          tomcat8
# Required-Start:    $local_fs $remote_fs $network $syslog
# Required-Stop:     $local_fs $remote_fs $network $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start/Stop Apache Tomcat 8
### END INIT INFO

TOMCAT_USER=tomcat8
TOMCAT_DIR=/opt/tomcat8

case "$1" in
start)
  echo "Starting Apache Tomcat 8..."
  su - $TOMCAT_USER -s /bin/bash -c "$TOMCAT_DIR/bin/startup.sh"
  exit $?
  ;;
stop)
  echo "Stopping Apache Tomcat 8..."
  su - $TOMCAT_USER -s /bin/bash -c "$TOMCAT_DIR/bin/shutdown.sh"
  exit $?
  ;;
restart)
  $0 stop
  $0 start
  exit $?
  ;;
*)
  echo "Usage: /etc/init.d/tomcat8 {start|stop|restart}"
  exit 1
  ;;
esac
exit 0
```

&emsp;&emsp;最后启动服务

```
sudo chmod 755 /etc/init.d/tomcat8                    # make it executable
sudo systemctl daemon-reload
sudo service tomcat8 start                            # run tomcat as service
sudo update-rc.d tomcat8 defaults                     # make tomcat run with system
```

&emsp;&emsp;最后可以使用一下命令启动/关闭/重启tomcat；
```
sudo service tomcat8 stop 
sudo service tomcat8 start
sudo service tomcat8 restart
```
&emsp;&emsp;如果你想要tomcat能被同一个局域网下的其他主机访问，你必须打开防火墙端口；windows在设置中有关闭防火墙，自行google；linux下使用ufw命令；如果你想要广域网上的其他主机通过IP地址访问你的tomcat，要么你的主机直接是有public ip，如果你在内网，你必须打开路由器的NAT，自行google；
```
sudo ufw allow 8000/tcp
sudo ufw status verbose
```

&emsp;&emsp;以上的设置是成功的，但是你无法再用自己的账户访问/opt/tomcat8了，因为他属于tomcat8用户（chown），这个很蛋疼；除非你切换到root用户。其实我们不一定要这样设置一个新用户来运行tomcat8，之所以这样设置是出于安全考虑的；下面来看看用户管理；
你会好奇这个命令是干什么？

```
sudo useradd -s /sbin/nologin tomcat8                 # add user to run tomcat service
```

&emsp;&emsp;这里关于用户管理只讲解本教程相关问题，更多请参考鸟哥私房菜；再一次提醒，***linux下的终端terminal不是shell***，千万不要被表面迷惑；我们都知道linux是***多用户多任务的系统***；但是由于现在我们都是用微机也就是个人电脑，基本上就你一个人在用，所以没有感觉什么叫多用户多任务；如果回到过去，没有盖茨和乔布斯的时代，那个时候个人是很难用到电脑的，你要用计算机，你得申请某一个时段，到机房去预约一个机位，在一个显示器面前使用命令行，登陆，然后进行相应任务，但你看到的并不是主机，那只是从大型主机连接过来的显示器或者说终端（现在你应该知道为啥叫terminal了吧），所以有很多个科学家登陆到同一台主机同一时间段做任务，这就需要主机操作系统可以进行多用户多任务管理了。

&emsp;&emsp;同样，现在，我们人手一台计算机，其他人不用了，就你一个人用，当然可以同时执行多个任务，这个在操作系统分时调度任务大家应该都知道，但是对于多用户，我们似乎了解的不多。并且如果你用的是Windows，你就更不可能很好的理解什么叫多用户了。如果是在linux下面，初学者也很少碰到多用户管理，因为一直是自己用。但是实际上，linux上有很多用户(**用户不一定是人，也可以是程序**)，并且有的还和你一起在同时使用linux呢。

&emsp;&emsp;现在在linux上，你使用Ctrl+Alt+T打开终端Terminal（记住那不是shell），严格来说这是一个终端模拟器，并不是真正的终端；真正的终端会占用整个显示器的，就是字符终端，不信你可以试试Ctrl+Alt+F1～F12，这是不同的终端。终端控制显示器，而不同的用户和任务会控制终端，当操作系统调度到该任务的时候，如果他需要用到终端显示器，此时终端控制权就给了该任务（比如用C语言写一个hello world程序，编译成可执行程序，运行一下，当操作系统调度到你的任务时，由于你需要使用标准输出即当前运行环境的终端，此时如果还有其他程序在使用终端显示器，而且你只有一个显示器，那么你的hello进程可能会被挂起，当然一般情况下是直接看到输出了，因为任务很简单）；因此，你登陆后其实默认只是你自己的账户，你也可以切换到root账户，但是你会发现目录什么的都变了。这就是多用户显示出来的区别了，现在root掌管了你的虚拟终端，并且你以root身份登陆到了shell中，即root用户使用shell（shell也有很多种，bash，zsh等等）控制了你的终端模拟器（就是你看到的那个黑框框），你会看到root@your-host-name:,对，这个就说明现在是root在掌管了。你可以exit，然后就回到你现在的用户了，变成了your-name@your-host-name对吧。但是你还是在同一个黑框框下面（记住他是虚拟终端而不是shell，shell是一个程序在运行，而terminal负责标准输入输出），你现在应该有了terminal和shell的区别的概念了吧，同时也知道了多用户的概念了吧。

&emsp;&emsp;不过你可能有疑惑，并没有实现多个用户同时登陆啊，是的，刚才每次只有一个用户在执行。其实你可以打开多个terminal，然后在每一个terminal中使用sudo su account-name来切换用户，这几个终端登陆不同的用户，结果，你发现几个不同的用户同时登陆了你的主机了。如果你的主机开放了端口并且可以当做服务器，别人如果有你的账户和密码，也是可以通过ssh username@host-ip（如果是内网直接ssh username@hostname也可以，需要配置映射）远程登陆到你的主机的（当然ssh是需要提前分发好秘钥的）。不信，如果你开通了腾讯云主机，你可以使用这种方法登陆你的云主机。我们如何添加一个用户呢？很简单使用adduser或者useradd就可以了。

&emsp;&emsp;再回到我们的主题，上面那个sudo useradd -s /sbin/nologin tomcat8中，-s /sbin/nolongin其实是设置这个用户的登陆方式。这里其实压根没有给tomcat8登陆方式，因为ubuntu16.04没有这个目录和shell。正确的应该是/usr/sbin/nologin或者/bin/false,你可以到相应的目录找到这两个程序，他们都是禁止登陆的。也就是你使用sudo su tomcat8没有用的，你不能登陆。

&emsp;&emsp;为什么设置一个不能登陆的用户？就是为了安全，前面说过，别人可以用账户名和密码远程登陆你的服务器的。这样别人就没办法入侵了。另外，如果你是在自己电脑上而不是服务器上，其实可以设置一个可以登陆的shell的；使用 less /etc/passwd可以看到你的tomcat用户的登陆方式，你可以使用sudo usermod -s /bin/bash tomcat8 来更改tomcat8的登陆shell。这样，你就可以登陆到tomcat8用户了，就可以访问/opt/tomcat8了。你现在可以直接进入/opt/tomcat8/bin目录，执行./startup.sh或./catalina.sh start或者./catalina.sh run(调试模式)就可以启动tomcat8了。其实开始讲的创建service模式也是调用这个目录下的命令程序。

&emsp;&emsp;以上单独创建tomcat8用启动tomcat8是为了在production server中部署，在本地开发环境中开发没必要；所以再次我把tomcat8用户删除了，并且tomcat目录用户权限全部改成自己常用的了，这样更方便开发，要不然每次到都要切换用户或者到root权限才能修改文件；
```
// 删除tomtcat8
sudo userdel tomcat8
// 修改tomcat8所属用户
sudo chown -R kinny:kinny /opt/tomcat8
sudo chown -R kinny:kinny /opt/tomcat8/
// tomcat8的执行权限可以不用修改，还是755

//最后将/etc/profile.d/tomcat8文件中的TOMCAT\_USER变量修改为your_account(例如我的是TOMCAT\_USER=kinny)
```

#### Step 3:setup user account
&emsp;&emsp;为了通过web方式管理webapp和tomcat，可以设置tomcat用户和角色
在/opt/tomcat8/conf目录下找到tomcat-users.xml,设置用户如下：
```
<role rolename="manager-gui"/>
<user username="manager" password="manager" roles="manager-gui"/>
<role rolename="admin-gui"/>
<user username="admin" password="admin" roles="manager-gui,admin-gui"/>

```
&emsp;&emsp;此时，你就可以通过主页上Manager APP和Host Manager输入用户名和密码就可以通过web管理tomcat了；


## Mysql JDBC Driver
```
sudo apt-get install libmysql-java

// 在该目录下会看到mysql-connector-java.jar
ll /usr/share/java/mysql-connector-java*
-rw-r--r-- 1 root root 987191 2月   7  2016 /usr/share/java/mysql-connector-java-5.1.38.jar
lrwxrwxrwx 1 root root     31 2月   7  2016 /usr/share/java/mysql-connector-java.jar -> mysql-connector-java-5.1.38.jar

// 拷贝到/usr/lib/jvm/java-8-oracle/jre/lib/ext/目录下（因为如果你的servlet程序需要访问mysql数据库，需要mysq-connector-java.jar包，就需要tomcat知道他在哪儿，为了简单，你可以直接拷贝到jre/lib/ext/库里，其实还可以设置环境变量，这是linux高级主题），记住不是tomcat8的lib，而是tomcat8使用的jre的lib
sudo cp /usr/share/java/mysql-connector-java-5.1.38.jar /usr/lib/jvm/java-8-oracle/jre/lib/ext/

```

## SQL injection
&emsp;&emsp;针对querybook.html 和QueryServlet,由于QueryServlet采用获取http querystring中的参数，比如author，然后构造sql语句执行，如果你知道了后台构造sql的方式，你就可以进行sql injection attack，比如你可以通过在querybooktextfield.html页面直接输入一下author name；就会得到所有的作者信息；或者直接使用url（http://localhost:8000/ebookshop/query?author=Ku%25%27+or+author+like+%27%25）攻击
```
// 正常查询
author name 输入
Kumar

You query is : select * from books where author like '%Kumar%' and qty > 0 order by author asc, title asc

Kumar, A Cup of Java, $55.55

==== 1recods found ====

// 攻击查询，整个数据库都被查出来了
author name 输入
Ku%' or author like '%

You query is : select * from books where author like '%Ku%' or author like '%%' and qty > 0 order by author asc, title asc

Kevin Jones, A Teaspoon of Java, $66.66

Kumar, A Cup of Java, $55.55

Mohammad Ali, More Java for more dummies, $33.33

Tan Ah Teck, Java for dummies, $11.11

Tan Ah Teck, More Java for dummies, $22.22

==== 5recods found ====

```

## 前端后端双层验证
&emsp;&emsp;一般情况下，我们给用户的输入是通过html，用户输入通过前端js等操作，此时js可以对用户的输入合法性等进行简单验证而不用麻烦服务端进行，此时也可以降低服务端请求压力。但是仅仅在客户端验证并不安全，因为有恶意用户可能不通过页面操作，而是自己通过js脚本或者其他方式（如直接在浏览器输入url及相应参数访问后台）发送get或者post请求，此时服务器端必须进行再次验证才能保证安全性；

## HttpSession
&emsp;&emsp;Http协议中说他自己时stateless即无状态的，什么意思？就是你的前一个请求是用户登陆，后一个请求是加入购物车（但是后一个请求并不知道你登陆了），请求之间并不能相互获取状态数据。如何解决这个问题？我们在设计一个类的时候，类的行为或者说函数会多次执行或者调用，类如何保证每次调用都知道当前自己状态（即上一次执行后的状态）呢？类的状态由成员变量来维持！同样，为了保持HTTP请求之间知道状态，你必须提供状态数据供每一次请求参考，这个数据状态的维持就抽象成了数据共享存储模型的设计，无非就是内存/文件/数据库（本质上也是文件）/消息 来维持了，就是现在比较流行的三种方式，客户端cookie（文件），服务端session（内存），数据库table。这样每一次Http请求都带上或者查询这些状态数据，就实现了状态的维持了！为什么不能用消息（比如上一次Http请求完成后，将数据以消息形式异步发送给下一个请求），我猜测可能的原因是消息太复杂了，没必要（比如你发一个消息给下一个HTTP请求，关键是下一个HTTP请求什么时候开始是由客户端确定的啊，他可能立即发送下一个，也可能不再发送了，这就有需要维护消息的生命周期啊）。消息发送接受还不是要开辟一块内存，不如直接存储在内存中供大家使用，使得程序更复杂，因此该场景不适合用消息。

&emsp;&emsp;不过，目前有结合三种方式（cookie session database）一起使用，从而优化用户体验！

&emsp;&emsp;发现如果不登陆京东，直接加入购物车，最多只能加50个item，此时只是在本地使用了cookie存储item；另外，如果你在chrome中禁用cookie，那么不登陆加入购物车的功能基本上是不能用的。而且而且很重要的一点，如果你禁用了cookie，京东根本无法登陆啊，有图有真相。这说明京东默认大家都使用cookie，并且登陆后设置使用cookie和user关联起来，当登陆以后，这些item会被添加到你的登陆后的购物车，并且云端同步到手机客户端，这说明登陆后在服务端存储了item，猜测可能时memcache或者database存储的；

![jd-no-cookie-no-login](https://github.com/kitianFresh/yaebsdbcp/blob/yaebsum/images/https://github.com/kitianFresh/yaebsdbcp/blob/yaebsum/images/jd_no_cookie_no_login.png)

&emsp;&emsp;tomcat8提供的servlet中的session management也是如此，HttpSession实际上是需要客户端打开cookie的，如果禁止，即使你在服务端使用request.getSession(),那也是一个新的session了。并不能维持会话状态，亲自测验过了。

&emsp;&emsp;如果你只是使用同一个浏览器发送请求，因为服务端将data存储到session的缘故，你的shopping cart状态得以维持，因为对于和shoppingcart相关的每一个请求，都会使用request.getSession获取这个全局的session。取出数据并返回给客户端；但是如果是换了另一个浏览器呢？

&emsp;&emsp;我同时使用chrome和firefox进行购物车操作，发现尽然互不影响，这说明tomcat8 web container使用了方法来辨别request来自是哪一个浏览器；经过测验之后发现，其实在/start对应的EntryServlet和/search对应的QueryServlet还有/checkout对应的CheckoutServlet都使用request.getSession(false);//已经存在就返回，不存在什么都不干！（因为他们的业务只是读取购物车，不包含新建一个购物车） 而在CartServlet中使用request.getSession(true);//已经存在就返回，不存在则创建一个！

![start](https://github.com/kitianFresh/yaebsdbcp/tree/yaebsum/images/start.png)
&emsp;&emsp;第一次请求/start，此时客户端的request肯定不含cookie，因为此业务代码cookie是由服务端的response通过set-cookie告知客户端的。选择相应作者后点击SEARCH；

![search](https://github.com/kitianFresh/yaebsdbcp/blob/yaebsum/images/search.png)
&emsp;&emsp;第二次请求/search，此时也还是没有cookie项；因为还没有触发服务端cookie的设置。我们选择book并加入购物车；

![cart](https://github.com/kitianFresh/yaebsdbcp/blob/yaebsum/images/cart.png)
&emsp;&emsp;第三次请求/cart，我们看到此次request的返回response有一个Set-Cookie项，因为服务器在CartServlet中使用request.getSession(true);//已经存在就返回，不存在则创建一个！也就是说服务器开始创建session了，并且默认产生了一个ID这个ID就是JSESSIONID；此时我们再点击Select More Books...链接；

![start](https://github.com/kitianFresh/yaebsdbcp/blob/yaebsum/images/start-cookie-request.png)
&emsp;&emsp;第四次我们再次请求/start，注意看此时的request，里面多了一个cookie项偶！这个里面装的即使服务端告知客户端的那个JSESSIONID了；

![JSESSIONID](https://github.com/kitianFresh/yaebsdbcp/blob/yaebsum/images/JSESSIONID.png)
&emsp;&emsp;我们可以在chrome debug tool中的network中找到cookie，查看他的各个字段；


&emsp;&emsp;以上代码可以在[这里](https://github.com/kitianFresh/yaebsdbcp)找到；实验也证明合理,我们在（没有请求过/cart前提下）请求/start和/search时，request和response header中都没有Cookie，因为他们都不创建session；但是当我第一次请求/cart时，response header中多了个Set-Cookie，因为第一次请求CartServlet是要创建新session的；此后一系列的request header中都自动添加了Cookie了；重要的事情来了，Tomcat8 中的Servlet都是单例模式，他是如何知道request来自哪里，又是如何轻而易举的通过request.getSession()获取到客户端想要的那个session呢？查看Set-Cookie选项你就知道了，原来服务端在创建Session的时候生成了一个JSESSIONID=4525C1896DCB743808B956F3EF9DC623唯一标示，这样servlet就可以轻而易举的维持不同session了；因为每一个请求都自带了这个ID。也就是说，**Servlet Session的实现默认依赖于客户端的cookie设置**,如果你要在禁止cookie的情况下也能实现会话，那么你需要做额外处理了。比如根据IP唯一表示（但ip也可能变化，一般不用），或者让用户登陆 使用userid做唯一标示，或者将unique id隐藏在html form中（但是不适用与纯链接href，点击纯链接并不会发送一个附加的id参数），最后一种方法是每一次都通过url附带这个JSESSIONID参数发送出去，即url rewriting，使用的是response.encode(url),非常复杂，因为你得将所有的action或者link都动态生成，不管是动态页面还是静态页面中的url，你都得一个个进行url rewriting。[url rewriting 版本](https://github.com/kitianFresh/YaEshop)

&emsp;&emsp;linux下 的chrome开启多个窗口默认共享同一个域下的cookie； 采用url rewriting写的代码很难维护，必须要小心哪里没有进行url重写；

>A servlet should be able to handle cases in which the client does not choose to join a session, such as when cookies are intentionally turned off. Until the client joins the session, isNew returns true. If the client chooses not to join the session, getSession will return a different session on each request, and isNew will always return true.

## Authentication and Authorization
### 认证
&emsp;&emsp;Tomcat容器做认证不需要应用本身认证，最简单的tomcat-user.xml和server.xml中就有manager的认证配置，默认采用的是UserDatabaseRealm，他需要配套tomcat-user.xml中的角色配置。其实就是所有的用户和角色都配置到tomcat-user.xml中，然后再通过server.xml告诉tomcat容器使用的Realm是UserDatabaseRealm；最后当然还要通过web.xml来具体限制资源访问权限；但是UserDatabaseRealm一般只是开发时候这样配置，如果是production，你最好使用数据库形式。

&emsp;&emsp;你也可采用数据库的形式，即JDBCRealm，就是用户密码角色存储在数据库中，然后配置；此时就是在server.xml中配置说Realm采用的是JDBC就行了，然后设置相应的数据库参数，当然数据库的设计遵循一定的规则，这个可以参考官方文档；此时就不用在tomcat-user.xml中配置用户了，你需要将用户数据插入到数据库中；应用资源访问权限设置和前者一样；

&emsp;&emsp;这里要特别注意的是你在修改server.xml后必须重启tomcat；但是如果在eclipse中开发即使重启也没有用，你必须删除原来的server，重新选择server，然后重启eclipse才行。

&emsp;&emsp;这里删除server后遇到一个问题，就是不能选择tomcat8了，我的环境是ubuntu16.04和eclipse neon，本地安装的是tomcat8.0.37；
  1. 关闭eclipse；
  2. 你需要进入%eclipse_workspace%/.metadata/.metadata/.plugins/org.eclipse.core.runtime/.settings,然后删除org.eclipse.jst.server.tomcat.core.prefs和org.eclipse.wst.server.core.prefs；
  3. 进入Window-->preferences->server->runtime environment->add 一个tomcat；选择%CATALINA_HOME%就行了；
  4. 如果在运行server的时候出现问题，那你就把%CATALINA_HOME%/conf/中的文件全部复制到%eclipse_workspace%/Servers/Tomcat V8.0 Server at localhost-config目录下面；但是最好的方法是将这几个文件软链接到正真的%CATALINA_HOME%/conf/下面的相同文件，这样改动就只用改动一处了；如果是在默认情况下，eclipse实际上会通过/usr/share/tomcat8/conf/来找conf文件，但是手动安装的tomcat8在你自己的目录下面，你需要创建软链接；
```
cd /usr/share/tomcat8
sudo ln -s /%CATALINA_HOME%/conf conf
sudo chmod -R a+rwx /usr/share/tomcat7/conf
```

## EJB
### JBOSS安装
&emsp;&emsp;由于JBOSS版本变化太大了，现在有两种JBOSS，一个是商业版本JBOSS，一个是社区免费版本的叫wildfly；我们下载wildfly 10.1final；
```
//最好能在terminal下面翻墙，要不然速度很慢
wget http://download.jboss.org/wildfly/10.1.0.Final/wildfly-10.1.0.Final.tar.gz

tar -zxvf wildfly-10.1.0.Final.tar.gz

//设置环境变量，和tomcat java一样,不过可以不用设置，现在设置这个主要是为了可以直接在其他目录运行standalone.sh，不设置的话直接到JBOSS_HOME/bin目录下面运行相应命令即可
//系统级环境变量设置，打开/etc/profile
export JBOSS_HOME=/opt/wildfly-10.1.0.Final

//运行
./standalone.sh
//访问localhost:8080
```
&emsp;&emsp;其他教程看[官网](https://docs.jboss.org/author/display/WFLY10/Getting+Started+Guide)，很详细，不罗嗦了。

&emsp;&emsp;另外，在github上提供了大量的[quickstart examples](https://github.com/wildfly/quickstart/tree/10.x#build-and-deploy-the-quickstarts)可供学习，里面有详细的安装部署说明，可以试着运行里面的例子；

### eclipse中add JBOSS Server
&emsp;&emsp;这里有一个坑，JBOSS和Tomcat不一样，可能不能在preferences->server->runtime environment中直接add wildfly10.x,尽管你已经安装了wildfly但是还是不行，会出现The import javax.ejb cannot be resolved；

&emsp;&emsp;需要下载JBOSS tool进入help->Eclipse Marketplace,输入Jboss tool进行搜索，然后安装JBOSS final工具；

&emsp;&emsp;结果出现错误，安装GTK即可，apt-get install libwebkitgtk-3.0-0；
```
Webkit is not installed.
To use JBoss Tools Central please install libwebkitgtk library with your favourite package manager.
To install it execute the following command:
	For GTK2
		Fedora, RHEL - yum install webkitgtk
		Ubuntu - apt-get install libwebkitgtk-1.0-0
	For GTK3
		Fedora, RHEL - yum install webkitgtk3
		Ubuntu - apt-get install libwebkitgtk-3.0-0
```
### eclipse中运行EJB
#### EJB invocations from a remote client using JNDI
&emsp;&emsp;老师的教程太老了，你运行的话保证你运行不了！现在的JNDI中的jndi.properties语法有点不一样了！我运行了一下午。。。终于发现解决方法了！

&emsp;&emsp;你在创建完成EJB Project之后，需要先导出到wildfly中，右键项目，Export->EJB->EJB jar;作为一个jar包导出到JBOSS\_HOME/standalone/deployments/目录下面（最好先运行jboss）;部署完成之后，就可以创建客户端程序了；

&emsp;&emsp;新建一个Java Project;然后注意，这里有坑！完成客户端程序之后，你会发现客户端代码里面的NumberService哪里来的啊，对于新手来说，以为是EJB自己找到的，恩，老师是这样讲的，但是其实你会发现的代码报错！因为压根找不到NumberService，所以你需要把NumberService的接口放到客户端才行！

&emsp;&emsp;第二个坑，这个Java Project压根没有依靠ejb的部分啊，只有jre，怎么能找到ejb呢？ 因为你还需要一个jar包，给客户端用，jboss-client.jar,这个包在JBOSS_HOME/bin/client目录下面，右键点击项目build path->confiuration->add external jar

&emsp;&emsp;还有我把mappedName也没用，因为也有错误，压根找不到NumberCreator，干脆去掉了，直接采用真实的类名访问的NumberService service = (NumberService)context.lookup("EJBTest/NumberServiceBean!bean.NumberService")；

&emsp;&emsp;最后一个坑，不太清楚客户端的目录结构，结果把jndi.properties放在了错误的目录里，应该放在src/根目录下面，要不然报错
```
Exception in thread "main" javax.naming.NoInitialContextException: Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial
```

&emsp;&emsp;jndi.properties的内容，以前的jnp不再使用了，[新的语法在这里](https://docs.jboss.org/author/display/WFLY8/Remote+EJB+invocations+via+JNDI+-+EJB+client+API+or+remote-naming+project),另一个是[博客](https://blog.akquinet.de/2014/09/26/jboss-eap-wildfly-three-ways-to-invoke-remote-ejbs/)：
```
java.naming.factory.initial=org.jboss.naming.remote.client.InitialContextFactory
java.naming.provider.url=http-remoting://localhost:8080
jboss.naming.client.ejb.context=true
```
&emsp;&emsp;后来的一篇良心博客[A Simple Enterprise JavaBeans 3.1 Example with WildFly 9 (and Eclipse)](https://eai-course.blogspot.com/2012/10/a-simple-enterprise-javabeans-31.html?showComment=1478609438605#c3959201283805407567)

```
java.naming.factory.initial=org.jboss.naming.remote.client.InitialContextFactory
java.naming.provider.url=http-remoting://localhost:8080
jboss.naming.client.ejb.context=true
```

###j2ee认证参考
  - [Authentication and Authorization](https://developer.appway.com/screen/ViewDocument/bookId/1272417268326?path=1272417268326|1258947867223|1258947867336&language=en&searchQuery=Screen%20Editor%20Guide&versionFilter=LatestCommittedFilter)
  - [Enable Basic Authentication for webapps in Apache Tomcat 8 - PART 1](http://sureshatt.blogspot.com/2016/05/enable-basic-authentication-for-webapps.html)
  - [Enable Basic Authentication for webapps in Apache Tomcat 8 - PART 2 (Password Hashing)](http://sureshatt.blogspot.com/2016/05/enable-basic-authentication-for-webapps_16.html)
  - [How do I use a JDBC Realm with Tomcat and MySQL?](http://www.avajava.com/tutorials/lessons/how-do-i-use-a-jdbc-realm-with-tomcat-and-mysql.html?page=2)
  - [Could not load the Tomcat server configuration](http://stackoverflow.com/questions/30962732/could-not-load-the-tomcat-server-configuration)
  - [Production Server Overview](http://www.unidata.ucar.edu/software/thredds/current/tds/tutorial/Security.html)

## 参考
  - [How to Install Apache Tomcat 8.0.x on Linux](http://linoxide.com/linux-how-to/install-tomcat-8-0-x-linux/)
  - [Setting up environment for JEE development under Ubuntu/Debian](http://sukharevd.net/environment-for-j2ee-development-under-ubuntu.html#tomcat)
  - [defference between /sbin/nologin and /bin/false](http://serverfault.com/questions/519215/what-is-the-difference-between-sbin-nologin-and-bin-false)
  - [Does /usr/sbin/nologin as a login shell serve a security purpose?](http://unix.stackexchange.com/questions/155139/does-usr-sbin-nologin-as-a-login-shell-serve-a-security-purpose)
  - [How To Install Apache Tomcat 8 on Ubuntu 16.04](https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-8-on-ubuntu-16-04)
