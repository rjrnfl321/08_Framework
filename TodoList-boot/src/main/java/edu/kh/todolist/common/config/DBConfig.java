package edu.kh.todolist.common.config;

import javax.sql.DataSource; 

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/* @Configuration 어노테이션
 * - 서버 실행 시 자동으로 객체로 만들어져
 *   내부에 작성된 메서드를 모두 실행하게함
 * 
 * @Bean 어노테이션
 * - 메서드 수행 결과로 반환 객체들을 bean으로 등록함
 *  + 메서드 내부 코드 == bean 설정 코드
 *  
 * @PropertySource :  properties 파일의 내용을 이용하겠다는 어노테이션
 */

@Configuration // 서버 실행시 자동으로 객체 생성, 내부작성된 메서드 모두 실행

@PropertySource("classpath:/config.properties")
// classpath에 위치하는 config.properties의 내용을
// @PropertySource 어노테이션을 사용하여
// 설정 파일을 이용할수 있게 해줌


public class DBConfig {

	@Autowired // 등록된 Bean 중에 같은 자료형 객체를 의존성 주입 (DI)
	
	private ApplicationContext applicationContext;
  // 이 클래스에서 ApplicationContext라는 종류의 정보를 담을 준비를 하고
	// 외부에서 접근할수 없도록 함
	
	// @Bean
	// - 개발자가 수동으로 bean을 등록하는 어노테이션
	// - @Bean 어노테이션이 작성된 메서드에서 반환된 객체는
	// Spring Container가 관리함(IOC)

	@Bean
	// @ConfigurationProperties(prefix = "spring.datasource.hikari")
	// properties 파일의 내용을 이용해서 생성되는 bean을 설정하는 어노테이션
	// prefix를 지정하여 spring.datasource.hikari으로 시작하는 설정을 모두 적용
	
	// @ConfigurationProperties : 외부 설정 파일의 값을 Java 객체에 자동으로 매핑
	//													 -> 설정 값을 코드처럼 다루고 구조화하여 관리 가능
	
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	// prefix = "spring.datasource.hikari"
	// 설정 파일에서 spring으로 시작하는 모든 설정을 이 클래스와 매핑함
	
	public HikariConfig hikariConfig() {
		return new HikariConfig(); // 새로운 HikariConfig 객체 생성하고
															 // 메서드 호출한 곳으로 반환(return)한다
	}

	// DataSource : Java의 표준 인터페이스 DB 연결 생성 및 관리하는 기능
	
	@Bean
	public DataSource dataSource(HikariConfig config) {
		// 메서드의 매개변수로 HikariConfig config 받는 이유?
		// - 메서드가 DataSource 객체를 생성할때 필요한 설정 정보들을
		//   외부에서 받아와 사용하기 위해서  
		
		DataSource dataSource = new HikariDataSource(config);
		// DB 연결을 관리하는 new HikariDataSource 객체 생성하고
		// dataSource 변수에 저장
		
		return dataSource; // dataSource 객체를 메서드 호출한 곳으로 반환 
	}

	////////////////////////////Mybatis 설정 추가 ////////////////////////////
	
	// sqlSession : DB연결 + SQL 파일 위치 등록 + 마이바티스 설정 적용 + 클래스 별칭 등록
	
	// SqlSessionFactory : SqlSession을 만드는 객체 
	@Bean
	public SqlSessionFactory sessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();

		sessionFactoryBean.setDataSource(dataSource);

		// 매퍼 파일이 모여있는 경로 지정
		sessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mappers/**.xml"));

		// 별칭을 지정해야하는 DTO가 모여있는 패키지 지정
		// -> 해당 패키지에 있는 모든 클래스가 클래스명으로 별칭이 지정됨
		sessionFactoryBean.setTypeAliasesPackage("edu.kh.todolist");
		
		// 별칭 지정 전 -> edu.kh.demo.dto.User   라고 작성
		// 별칭 지정 후 -> User                   라고만 작성
		

		// 마이바티스 설정 파일 경로 지정
		sessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));

		// SqlSession 객체 반환
		return sessionFactoryBean.getObject();
	}

	// SqlSessionTemplate :  기본 SQL 실행 + 트랜잭션 처리
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sessionFactory) {
		return new SqlSessionTemplate(sessionFactory);
	}

	// DataSourceTransactionManager : 트랜잭션 매니저
	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

}
